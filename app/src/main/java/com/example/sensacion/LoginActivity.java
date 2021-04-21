package com.example.sensacion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    /*Atributos de la clase*/
    private TextInputEditText tietCorreo, tietContrasenia;

    /* Agregamos una alerta y un progress para interactuar con el usuario*/
    private AlertDialog.Builder alerta;
    private ProgressDialog progress;

    /*Objeto Volley (Requestqueue) - Conexion al servidor*/
    private RequestQueue conexionServidor;

    /*Objeto StringRequest - Petición al servidor*/
    private StringRequest peticionServidor;

    /*Base url del servicio*/
    private final String BASE_URL = "http://dtai.uteq.edu.mx/~luivid195/AWOS/servicios/Store-Genship/back/";


    /* Para acceder al archivo global SharedPreferences necesitamos
    una refernecia a un objeto de tipo SharedPreferences*/
    private SharedPreferences prefs;

    /*Para escribir (agregar, modificar o eliminar) una entrada de SharePreferences Necesitamos un atributo de tipo Editor */
    private SharedPreferences.Editor prefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Vincular los atributos de la clase con los componentes de Layout (.xml)*/
        tietCorreo       = findViewById(R.id.tiet_correo);
        tietContrasenia  = findViewById(R.id.tiet_contrasenia);

        /*Objetos*/
        alerta           = new AlertDialog.Builder(LoginActivity.this);
        progress         = new ProgressDialog(LoginActivity.this);
        conexionServidor = Volley.newRequestQueue(LoginActivity.this);

        /*Inicializamos la refernecia al archivo SharedPreferences
        * El método getSharePreferences usa dos parámetros
        * 1.- El nombre del workspace
        * 2.- El tipo de apertura
        */

         prefs = getSharedPreferences("sensacion_datos", MODE_PRIVATE);

         /*Generamos una variable de edición*/
         prefsEditor = prefs.edit();

         /*Una vez que se genero la referncia a nuestro espacio de trabajo
         Podemos agregar claves/valores por medio del método .set{_TIPO_DATOS}()
         Podemos agregar claves/valores por medio del método .get{_TIPO_DATOS}()
         */

        final String SPcorreo = prefs.getString("correo", null);

        /*Si el valor de idUsuario ES DIFERENTE A NULO
        Redireccionamos al Home */

        if ( SPcorreo != null){
            startActivity(new Intent(
                    LoginActivity.this,
                    MainActivity.class
            ));
        }
    }

    /*Método para ir al home (Drawer)*/
    public void navHome(View v){
        startActivity(new Intent(
                LoginActivity.this,
                MainActivity.class
        ));
    }

    /*Método para ir al registro*/
    public void navRegistro(View v){
        startActivity(new Intent(
                LoginActivity.this,
                RegistroActivity.class
        ));
    }

    /*Deshabilitar el boton de retorno (BackButton) */
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this,"Operación no permitida", Toast.LENGTH_SHORT).show();
        return;
    }

    /**
     * Método para iniciar sesión desde un usuario en una bd remota
     * por medio de un servicio web
     */

    public void login (View v)
    {
        /*Validación de campos*/
        final String correo      = tietCorreo.getText().toString();
        final String contrasenia = tietContrasenia.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches())
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Correo electrónico inválido");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        if(contrasenia.trim().length() < 8)
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Contraseña inváida");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Mostrarmos una ventana en estado de carga*/
        progress.setTitle("Autentificando");
        progress.setMessage("Por favor espere...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        /*Consumir el servicio de login*/
        peticionServidor = new StringRequest(
                /*Método de envio*/
                Request.Method.POST,
                /*URL del servicio*/
                BASE_URL + "autentificacion/login",
                /*Acciones cuando el servidor conteste errores*/
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Crear un objeto json para tomar la respuesta*/
                        progress.hide();
                        try {
                            JSONObject objRespuesta = new JSONObject(response);

                            /*Si el response_code es 200 significa que
                            el usuario y la contraseña son correctos*/
                            if (objRespuesta.getInt("respuesta") == 200) {
                                /*Creamos un objeto de los datos del usuario*/

                                JSONObject datosUsuario = objRespuesta.getJSONObject("cliente");

                                /*Escribimos en nuestro archivo SP el correo del usuario */
                                prefsEditor.putString("correo", datosUsuario.getString("correo"));
                                prefsEditor.putString("idCliente", datosUsuario.getString("idCliente"));

                                /*Guardamos los cambios en el archivo*/
                                prefsEditor.commit();

                                alerta.setTitle("Bienvenido")
                                        .setMessage("Hola de nuevo\n" + datosUsuario.getString("nombre"))
                                        .setIcon(R.drawable.hello)
                                        .setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }

                            /*Si el response code es 404 significa que el usuario / contraseña son incorrectos*/
                            if (objRespuesta.getInt("respuesta") == 404) {
                                alerta.setTitle("Error")
                                        .setMessage("Correo electrónico / contraseña incorrectos ")
                                        .setIcon(R.drawable.advertencia)
                                        .setCancelable(false)
                                        .setNeutralButton("Continuar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                tietContrasenia.setText("");
                                                /*Regresamos el cursor al campo del correo electrónico*/
                                                tietCorreo.requestFocus();

                                            }
                                        })
                                        .show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                },
                /* Si el servidor contesta con un error*/
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hide();
                        Toast.makeText(
                                LoginActivity.this,
                                error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*Indicamos los valores que se van a enviar el servicio*/
                params.put("correo", tietCorreo.getText().toString());
                params.put("contrasenia", AppHelper.MD5_Hash(tietContrasenia.getText().toString()));
                return params;
            }
        };
        conexionServidor.add(peticionServidor);
    }
}
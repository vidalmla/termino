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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    /*Atributos de la clase*/
    private Button btnRegresar;

    private TextInputEditText
            tietNombre, tietTelefono, tietConfirmaTel, tietCorreo, tietComfirmaCorreo,  tietContrasenia, tietConfirmaContra;

    /* Agregamos una alerta y un progress para interactuar con el usuario*/
    private AlertDialog.Builder alerta;
    private ProgressDialog progress;

    /*Objeto Volley (Requestqueue) - Conexion al servidor*/
    private RequestQueue conexionServidor;

    /*Objeto StringRequest - Petición al servidor*/
    private StringRequest peticionServidor;

    /*Base url del servicio*/
    private final String BASE_URL = "http://dtai.uteq.edu.mx/~luivid195/AWOS/servicios/Store-Genship/back/";


    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        /*Vincular los atributos de la clase con los componentes de Layout (.xml)*/
        btnRegresar         = findViewById(R.id.btn_regresar);

        tietNombre          = findViewById(R.id.tiet_nombre);
        tietTelefono        = findViewById(R.id.tiet_tel);
        tietConfirmaTel     = findViewById(R.id.tiet_confirmatel);
        tietCorreo          = findViewById(R.id.tiet_correo);
        tietComfirmaCorreo  = findViewById(R.id.tiet_confirmacorreo);
        tietContrasenia     = findViewById(R.id.tiet_contra);
        tietConfirmaContra  = findViewById(R.id.tiet_confirmacontra);

        /*Objetos*/
        alerta           = new AlertDialog.Builder (RegistroActivity.this);
        progress         = new ProgressDialog(RegistroActivity.this);
        conexionServidor = Volley.newRequestQueue(RegistroActivity.this);

        /*Inicializamos la referencia al archivo SP*/
        prefs       = getSharedPreferences("sensacion_datos", MODE_PRIVATE);
        prefsEditor = prefs.edit();


        /* Evento click para regresar al login*/
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Navegación por Intent*/
                startActivity(new Intent(
                        RegistroActivity.this,  //Clase actual.this
                        LoginActivity.class                    //Clase destino.class
                ));
            }
        });
    }

    public void registro(View v)
    {
       /*VALIDACIÓN DE CAMPOS*/

        /*Tomar el texto de los campos y guardarlo en variables finales*/
        final String nombre          = tietNombre.getText().toString();
        final String telefono        = tietTelefono.getText().toString();
        final String confirmaTel     = tietConfirmaTel.getText().toString();
        final String correo          = tietCorreo.getText().toString();
        final String confirmaCorreo  = tietComfirmaCorreo.getText().toString();
        final String contrasenia     = tietContrasenia.getText().toString();
        final String confirmaContra  = tietConfirmaContra.getText().toString();

        /*Evaluar si el contenido cumple con la regla de validación*/

        /*Validamos que el nombre tenga mínimo 3 dígitos*/
        if (nombre.trim().length() < 3)
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Nombre inválido");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            //Salimos del método (finalizamos su ejecución)
            return;
        }

        /*Validamos que el teléfono tenga 10 dígitos*/
        if (telefono.trim().length() < 10)
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Número de teléfono inválido");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Validamos que el teléfono coincida*/
        if (!confirmaTel.equals(telefono))
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("El número de teléfono no coincide");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Validamos la estructura del correo*/
        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches() )
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Correo electrónico inválido");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Validamos que el correo coincida*/
        if(!confirmaCorreo.equals(correo))
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Correo electrónico no coincide");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Validamos que la contraseña tenga 8 dígitos*/
        if(contrasenia.trim().length() < 8)
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("Contraseña inválida");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Validamos que el contraseña coincida*/
        if(!confirmaContra.equals(contrasenia))
        {
            alerta.setTitle("ERROR");
            alerta.setMessage("La contraseña no coincide");
            alerta.setIcon(R.drawable.advertencia);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Aceptar", null);
            alerta.show();

            return;
        }

        /*Mostrarmos una ventana en estado de carga*/
        progress.setTitle("Conectando");
        progress.setMessage("Por favor espera...");
        progress.setCancelable(false);
        progress.setIndeterminate(true);
        progress.show();

        /*Invocamos la petición al sevidor con la url del registro */

        peticionServidor = new StringRequest(
                /*Método de envio*/
                Request.Method.POST,
                /*URL del servicio*/
                BASE_URL + "Autentificacion/registro",
                /*CODIGO PARA EL CASO EXITOSO*/
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Crear un objeto json para tomar la respuesta*/
                        progress.hide();
                        try {
                            JSONObject objRespuesta = new JSONObject(response);

                            /*Si la respuesta es 200 iniciamos sesion*/
                            if (objRespuesta.getInt("respuesta") == 200) {

                                final String idCliente =  objRespuesta.getString("idCliente");

                                alerta.setTitle("Bienvenido")
                                        .setIcon(R.drawable.hello)
                                        .setMessage("Tu registro fue éxitoso\n" + nombre )
                                        .setPositiveButton("Iniciar sesión", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                /*Escribimos en nuestro archivo SP el correo del usuario */
                                                prefsEditor.putString("correo", correo);
                                                prefsEditor.putString("idCliente", idCliente );

                                                /*Guardamos los cambios en el archivo*/
                                                prefsEditor.commit();
                                                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }

                            /*Si el response code es 404 significa que no se pudo realizar el registro*/
                            if (objRespuesta.getInt("respuesta") == 404) {
                                alerta.setTitle("Error")
                                        .setMessage("Lo sentimos :(\n" + nombre + ", algo salió mal" )
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                /*ingresar nuevamente los valores en los campos*/
                                                tietNombre.setText(nombre);
                                                tietTelefono.setText(telefono);
                                                tietConfirmaTel.setText(confirmaTel);
                                                tietCorreo.setText(correo);
                                                tietComfirmaCorreo.setText(confirmaCorreo);
                                                tietContrasenia.setText("");
                                                tietConfirmaContra.setText("");
                                                /*Regresamos el cursor al campo del correo electrónico*/
                                                tietCorreo.requestFocus();
                                            }
                                        })
                                        .setIcon(R.drawable.advertencia)
                                        .setCancelable(false)
                                        .show();
                            }
                            /*Si el response code es 400 significa que el usuario / contraseña son incorrectos*/
                            if (objRespuesta.getInt("respuesta") == 400) {
                                alerta.setTitle("Error")
                                        .setMessage("Está tratando de registrar una cuenta existente" )
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                /*ingresar nuevamente los valores en los campos*/
                                                tietNombre.setText(nombre);
                                                tietTelefono.setText(telefono);
                                                tietConfirmaTel.setText(confirmaTel);
                                                tietCorreo.setText(correo);
                                                tietComfirmaCorreo.setText(confirmaCorreo);
                                                tietContrasenia.setText("");
                                                tietConfirmaContra.setText("");
                                                /*Regresamos el cursor al campo del correo electrónico*/
                                                tietCorreo.requestFocus();
                                            }
                                        })
                                        .setIcon(R.drawable.advertencia)
                                        .setCancelable(false)
                                        .show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(
                                    RegistroActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    },
                /*CODIGO PARA EL CASO ERROR*/
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistroActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        progress.hide();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                /*Generamos un HASH (clave, valor) con los datos que deseamos enviar al servicio */

                Map<String, String> params = new HashMap<>();
                /*Agregamos cada elemento como variable del servicio*/
                params.put("nombre", nombre);
                params.put("telefono", telefono);
                params.put("correo", correo);
                params.put("contrasenia", AppHelper.MD5_Hash(contrasenia));

                return params;
            }
        };
        conexionServidor.add(peticionServidor);
    }
}
package com.example.sensacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductoActivity extends AppCompatActivity {

    /*Elementos layout activity-producto*/
    private ImageView imagen;
    private TextView articulo;
    private TextView descripcion;
    private TextView categoria;
    private TextView precio;
    private Button meGusta;

    /*Id del producto seleccionado*/
    private String idArticulo;

    /*Conexión servidor*/
    private RequestQueue conServidor;
    private StringRequest petServidor;

    /*Interacción usuario*/
    private AlertDialog.Builder alerta;
    private SwipeRefreshLayout srlArticulo;

    private SharedPreferences prefs;

    private String BASE_URL= "http://dtai.uteq.edu.mx/~luivid195/AWOS/servicios/Store-Genship/back/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        /*Inicialización de los elementos gráficos*/
        imagen      = findViewById(R.id.iv_imagen_producto);
        articulo    = findViewById(R.id.tv_nombre_articulo);
        categoria   = findViewById(R.id.tv_categoria_articulo);
        descripcion = findViewById(R.id.tv_descripcion_articulo);
        precio      = findViewById(R.id.tv_precio_articulo);
        meGusta     = findViewById(R.id.btn_megusta);


        /*Inicializamos la referencia al archivo SP*/
        prefs       = getSharedPreferences("sensacion_datos", MODE_PRIVATE);

        /*Alerta*/
        alerta = new AlertDialog.Builder(ProductoActivity.this);

        /*Servidor*/
        conServidor = Volley.newRequestQueue(ProductoActivity.this);

        /*Activar boton back en la barra de título */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Tomar el valor del id del articulo que nos envían desde el fragment anterior*/
        idArticulo = getIntent().getStringExtra("idArticulo");

        /*Inicializamos los elementos de la UI*/
        srlArticulo = findViewById(R.id.srl_articulo);

        /* Invocación del servicio */
        srlArticulo.post(new Runnable() {
            @Override
            public void run() {
                srlArticulo.setRefreshing(true);
                cargaProducto();
            }
        });

        /* Actualizamos la lista con el gesto de arrastrar */
        srlArticulo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlArticulo.setRefreshing(true);
                cargaProducto();
            }
        });

    }

    /*Evento para las secciones del menu superior (derecho/izquierdo)*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        /*Usamos un switch para saber que elemento del menu estamos seleccionando (por medio de su id)*/
        switch(item.getItemId()) {
            //Si el elemento seleccionado es la flecha de retorno
            case android.R.id.home :
                /*Finalizamos este activity y retornamos al anterior*/
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Método para consumir el servicio que retorna un producto*/
    public void cargaProducto() {
        petServidor = new StringRequest(
                Request.Method.POST,
                BASE_URL +"articulo/getarticulo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* Si la respuesta es correcta verificamos que existan productos */
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            /* Si la respuesta es un objeto con productos*/
                            if (respuesta.getInt("respuesta") == 200) {

                                /*Creamos un arreglo de los datos del detalle del articulo*/
                                JSONObject detalles = respuesta.getJSONObject("articulo");

                                /*Obtenemos la url de la imagen*/
                                final String urlImagen = detalles.getString("urlImagen");

                                /*Mostramos la imagen*/
                                Picasso
                                        .get()
                                        .load(urlImagen)
                                        .into(imagen);


                                /*Obtenemos los datos del objeto del arreglo y lo vivnculamos en la parte gráfica*/
                                articulo.setText(detalles.getString("articulo"));
                                categoria.setText(detalles.getString("categoria"));
                                descripcion.setText(detalles.getString("descripcion"));
                                precio.setText(detalles.getString("precio"));


                            }

                            /*Si el response code es 404 significa que el articulo no existe*/
                            if (respuesta.getInt("respuesta") == 404) {
                                alerta.setTitle("Error")
                                        .setMessage("Lo sentimos :( el producto es inexistente o está agotado")
                                        .setIcon(R.drawable.advertencia)
                                        .setCancelable(false)
                                        .setNeutralButton("Continuar", null)
                                        .show();
                            }

                            srlArticulo.setRefreshing(false);

                        }
                        catch ( Exception e ){
                            Toast.makeText(ProductoActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            srlArticulo.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProductoActivity.this,
                                error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

        ){
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                /*Indicamos los valores que se van a enviar el servicio*/
                params.put("idarticulo", String.valueOf(idArticulo));
                return params;
            }
        };
        /*Ejecutamos el servicio*/
        conServidor.add(petServidor);
    }

    public void agregardeseo (View v) {
        /* Método para consumir el servicio "insertdeseos" */
        petServidor = new StringRequest(
                Request.Method.POST,
                BASE_URL +"listadeseos/insertdeseo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            /* Si la inserción fue exitosa*/
                            if (respuesta.getInt("respuesta") == 200) {
                                alerta.setTitle("Éxito")
                                        .setMessage("Se ha agregado un articulo a tu lista")
                                        .setIcon(R.drawable.information)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", null)
                                        .show();

                                meGusta.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_black, 0, 0, 0);
                            }

                            /*Si el response code es 404 significa que el articulo no se pudo insertar a su lista*/
                            if (respuesta.getInt("respuesta") == 404 ) {
                                alerta.setTitle("Error")
                                        .setMessage("Lo sentimos :( no se pudo agregar el articulo a tu lista")
                                        .setIcon(R.drawable.information)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }

                            /*Si el response code es 400 significa que el articulo ya existe el articulo en su lista*/
                            if (respuesta.getInt("respuesta") == 400 ) {
                                alerta.setTitle("Información")
                                        .setMessage("Este articulo ya exite en tu lista de desos")
                                        .setIcon(R.drawable.information)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }
                        catch ( Exception e ){
                            Toast.makeText(ProductoActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProductoActivity.this,
                                error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){

            /*Obtenemos la variable idCliente del nuestro SharedPreferences*/
            final String idCliente = prefs.getString("idCliente", null);

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*Indicamos los valores que se van a enviar el servicio*/
                params.put("idCliente", String.valueOf(idCliente));
                params.put("idArticulo", String.valueOf(idArticulo));
                return params;
            }
        };
        /*Ejecutamos la petición al servidor*/
        conServidor.add(petServidor);
    }
}
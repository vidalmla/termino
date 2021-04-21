package com.example.sensacion.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sensacion.ProductoActivity;
import com.example.sensacion.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CuentaFragment extends Fragment {

    private TextView username;
    private TextView email;
    private ImageView avatar;

    private SwipeRefreshLayout srlCuenta;

    private RequestQueue conServidor;
    private StringRequest petServidor;

    private SharedPreferences prefs;

    private AlertDialog.Builder alerta;

    private String BASE_URL= "http://dtai.uteq.edu.mx/~luivid195/AWOS/servicios/Store-Genship/back/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Referencia al fragmento
        View fragmentView = inflater.inflate(R.layout.fragment_cuenta, container, false);

        avatar   = fragmentView.findViewById(R.id.iv_avatar);
        username = fragmentView.findViewById(R.id.tv_nombre_usuario);
        email    = fragmentView.findViewById(R.id.tv_correo_usuario);
        srlCuenta= fragmentView.findViewById(R.id.srl_cuenta);

        conServidor = Volley.newRequestQueue(getContext());

        alerta = new AlertDialog.Builder(getActivity());

        /*Inicializamos la refernecia al archivo SharedPreferences*/
        prefs = getActivity().getSharedPreferences("sensacion_datos", Context.MODE_PRIVATE);

        /* Indicar el estado del SwipeRefesh como ocupado (cargando) */
        srlCuenta.post(new Runnable() {
            @Override
            public void run() {
                srlCuenta.setRefreshing(true);
                /* Cargamos los datos del cliente desde el Webservice */
                cargaCuenta();
            }
        });

        /* Actualizamos la lista con el gesto de arrastrar */
        srlCuenta.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlCuenta.setRefreshing(true);
                cargaCuenta();
            }
        });

        return fragmentView;
    }

    public void cargaCuenta (){
        petServidor = new StringRequest(
                Request.Method.POST,
                BASE_URL+"autentificacion/getcliente",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Verificamos lso datos del usuario*/
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            if(respuesta.getInt("respuesta") == 200)
                            {
                                JSONObject datosUsuario = respuesta.getJSONObject("cliente");

                                /*Obtenemos la url de la imagen*/
                                final String urlAvatar = datosUsuario.getString("urlAvatar");

                                /*Mostramos la imagen*/
                                Picasso
                                        .get()
                                        .load(urlAvatar)
                                        .into(avatar);

                                username.setText(datosUsuario.getString("nombre"));
                                email.setText(datosUsuario.getString("correo"));
                            }

                            /*Si el response code es 404 significa que no puede encontrar los datos*/
                            if (respuesta.getInt("respuesta") == 404) {
                                alerta.setTitle("Error")
                                        .setMessage("Algo salio mal :( Intenta salir e iniciar sesión de nuevo")
                                        .setIcon(R.drawable.advertencia)
                                        .setCancelable(false)
                                        .setNeutralButton("Continuar", null)
                                        .show();
                            }

                            srlCuenta.setRefreshing(false);
                        }
                        catch (Exception e )
                        {
                            Toast.makeText(getActivity(),
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            srlCuenta.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),
                                error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            /*Obtenemos la variable correo del nuestro SharedPreferences*/
            final String correo = prefs.getString("correo", null);

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*Indicamos los valores que se van a enviar el servicio*/
                params.put("correo", correo);
                return params;
            }

        };
        //Ejecutamos el servicio
        conServidor.add(petServidor);
    }
}
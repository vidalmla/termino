package com.example.sensacion.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sensacion.AppHelper;
import com.example.sensacion.ProductoActivity;
import com.example.sensacion.R;
import com.example.sensacion.components.Articulo;
import com.example.sensacion.components.ArticuloAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaDeseosFragment extends Fragment {

    /* ArrayList de tipo Articulo */
    private List<Articulo> articulos;

    private ListView lvDeseos;
    private com.example.sensacion.components.ArticuloAdapter ArticuloAdapter;

    private SwipeRefreshLayout srlDeseos;

    private RequestQueue conServidor;
    private StringRequest petServidor;

    private SharedPreferences prefs;
    private AlertDialog.Builder alerta;

    private String articuloEliminar;
    private String idCliente;

    private TextView tvLista;
    private ImageView ivLista;

    private String BASE_URL= "http://dtai.uteq.edu.mx/~luivid195/AWOS/servicios/Store-Genship/back/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Referencia al fragmento */
        View fragmentView = inflater.inflate(R.layout.fragment_lista_deseos, container, false);

        tvLista = fragmentView.findViewById(R.id.tv_lista);
        ivLista = fragmentView.findViewById(R.id.iv_lista);

        /*Inicializamos la refernecia al archivo SharedPreferences*/
        prefs = getActivity().getSharedPreferences("sensacion_datos", Context.MODE_PRIVATE);

        alerta = new AlertDialog.Builder(getActivity());

        /* Inicializacion el ListView */
        lvDeseos = fragmentView.findViewById(R.id.lv_deseos);

        /* Inicializacion del SwipeRefresh */
        srlDeseos= fragmentView.findViewById(R.id.srl_deseos);

        conServidor = Volley.newRequestQueue(getContext());

        /* Creacion de objetos de tipo Articulo para ejemplicar el listview */
        articulos = new ArrayList<>();

        /* Indicar el estado del SwipeRefesh como ocupado (cargando) */
        srlDeseos.post(new Runnable() {
            @Override
            public void run() {
                srlDeseos.setRefreshing(true);

                /* Cargamos los productos desde el Webservice */
                cargaProductos();
            }
        });

        /* Actualizamos la lista con el gesto de arrastrar */
        srlDeseos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlDeseos.setRefreshing(true);
                cargaProductos();
            }
        });

        /* Vinculacion del adaptador con el listview */
        ArticuloAdapter = new ArticuloAdapter (getActivity(), articulos);
        lvDeseos.setAdapter(ArticuloAdapter);

        /**Evento long para eliminar de la lista de deseos***/

        lvDeseos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                alerta.setTitle("¿Eliminar?")
                        .setMessage("¿Realmente deseas eliminar este articulo de tu lista?")
                        .setIcon(R.drawable.trash)
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*Obtenemos el id del articulo a eliminar*/
                                articuloEliminar = String.valueOf(articulos.get(position).getIdArticulo());

                                /*Ejecutamos el método eliminar producto*/
                                eliminarProducto();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return false;
            }
        });

        /*****************************************************/

        /*Obtenemos la variable idCliente del nuestro SharedPreferences*/
        idCliente = prefs.getString("idCliente", null);

        return fragmentView;
    }//ONCREATE

    /*Metodo para cargar los productos desde un Servicio usando Volley*/
    public void cargaProductos() {
        petServidor = new StringRequest(
                Request.Method.POST,

                BASE_URL+"listadeseos/getlistadeseos",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* Si la respuesta es correcta verificamos que existan productos */
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            /* Si la respuesta es un objeto con productos*/
                            if (respuesta.getInt("respuesta") == 200) {
                                /* Generar un arreglo de productos */
                                JSONArray productos = respuesta.getJSONArray("listadeseos");

                                /* Generamos la lista articulos*/
                                articulos = new ArrayList<>();
                                Articulo articulo;
                                for(int i = 0; i < productos.length(); i++) {
                                    /* Para cada elemento en el arreglo JSON
                                     * se crea un objeto de tipo articulo y
                                     * se agregarlo a la lista de articulos */
                                    JSONObject objArticulo = productos.getJSONObject(i);

                                    articulo = new Articulo();
                                    articulo.setIdArticulo(objArticulo.getInt("idArticulo"));
                                    articulo.setNombre(objArticulo.getString("articulo"));
                                    articulo.setDescripcion(objArticulo.getString("descripcion"));
                                    articulo.setPrecio(objArticulo.getDouble("precio"));
                                    articulo.setImagen(objArticulo.getString("urlImagen"));
                                    articulo.setCategoria(objArticulo.getString("categoria"));

                                    /* Agregar el objeto de tipo Articulo a la lista de articulos*/

                                    articulos.add(articulo);
                                }

                                /* Actualizacion grafica del de la lista*/
                                ArticuloAdapter = new ArticuloAdapter(
                                        getActivity(),
                                        articulos
                                );
                                lvDeseos.setAdapter(ArticuloAdapter);
                                ArticuloAdapter.notifyDataSetChanged();

                                ivLista.setVisibility(View.GONE);

                            }
                            else
                            {
                                articulos.clear();
                                /* Actualizacion grafica del de la lista*/
                                ArticuloAdapter = new ArticuloAdapter(
                                        getActivity(),
                                        articulos
                                );
                                lvDeseos.setAdapter(ArticuloAdapter);
                                ArticuloAdapter.notifyDataSetChanged();

                                tvLista.setText("Aún no has agregado productos a tu lista");
                                ivLista.setVisibility(View.VISIBLE);
                                Picasso
                                        .get()
                                        .load(R.drawable.listavacia)
                                        .into(ivLista);
                            }

                            srlDeseos.setRefreshing(false);
                        }
                        catch(Exception e) {
                            Toast.makeText(getActivity(), //Context para fragmentos
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            srlDeseos.setRefreshing(false);
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*Indicamos los valores que se van a enviar el servicio*/
                params.put("idCliente", idCliente );
                return params;
            }
        };
        //Ejecutamos el servicio
        conServidor.add(petServidor);
    }

    public void eliminarProducto () {
        petServidor = new StringRequest(
                Request.Method.POST,
                BASE_URL+"listadeseos/deletedeseo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            /* Si el delete fue exitoso*/
                            if (respuesta.getInt("respuesta") == 200) {
                                alerta.setTitle("Éxito")
                                        .setMessage("Se ha eliminado un articulo a tu lista")
                                        .setIcon(R.drawable.information)
                                        .setCancelable(false)
                                        .setNegativeButton(null, null)
                                        .setNeutralButton(null, null)
                                        .setPositiveButton("OK", null)
                                        .show();

                                cargaProductos();
                            }

                            /*Si el response code es 404 significa que existe un problema*/
                            if (respuesta.getInt("respuesta") == 404 ) {
                                alerta.setTitle("Error")
                                        .setMessage("Lo sentimos :( algo salió mal intenta más tarde")
                                        .setIcon(R.drawable.information)
                                        .setCancelable(false)
                                        .setNegativeButton(null, null)
                                        .setNeutralButton(null, null)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }

                            /*Si el response code es 400 significa que el articulo es inexistente en la lista*/
                            if (respuesta.getInt("respuesta") == 400 ) {
                                alerta.setTitle("Información")
                                        .setMessage("Lo sentimos :( no se pudo eliminar el articulo a tu lista")
                                        .setIcon(R.drawable.information)
                                        .setNegativeButton(null, null)
                                        .setNeutralButton(null, null)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }

                        }
                        catch ( Exception e ){
                            Toast.makeText(getActivity(),
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*Indicamos los valores que se van a enviar el servicio*/
                params.put("idCliente", idCliente );
                params.put("idArticulo", articuloEliminar );
                return params;
            }
        };
        //Ejecutamos el servicio
        conServidor.add(petServidor);
    }

}
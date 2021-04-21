package com.example.sensacion.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sensacion.ProductoActivity;
import com.example.sensacion.R;
import com.example.sensacion.components.Articulo;
import com.example.sensacion.components.ArticuloAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    /* ArrayList de tipo Articulo */
    private List<Articulo> articulos;

    private ListView lvCatalogo;
    private ArticuloAdapter ArticuloAdapter;

    private SwipeRefreshLayout srlCatalogo;

    private RequestQueue conServidor;
    private StringRequest petServidor;
    private String BASE_URL= "http://dtai.uteq.edu.mx/~luivid195/AWOS/servicios/Store-Genship/back/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Referencia al fragmento */
        View fragmentView = inflater.inflate(R.layout.fragment_catalogo, container, false);


        /* Inicializacion el ListView */
        lvCatalogo = fragmentView.findViewById(R.id.lv_catalogo);

        /* Inicializacion del SwipeRefresh */
        srlCatalogo = fragmentView.findViewById(R.id.srl_catalogo);

        conServidor = Volley.newRequestQueue(getContext());

        /* Creacion de objetos de tipo Articulo para ejemplicar el listview */
        articulos = new ArrayList<>();

        /* Indicar el estado del SwipeRefesh como ocupado (cargando) */
        srlCatalogo.post(new Runnable() {
            @Override
            public void run() {
                srlCatalogo.setRefreshing(true);

                /* Cargamos los productos desde el Webservice */
                cargaProductos();
            }
        });

        /* Actualizamos la lista con el gesto de arrastrar */
        srlCatalogo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlCatalogo.setRefreshing(true);
                cargaProductos();
            }
        });

        /* Vinculacion del adaptador con el listview */
        ArticuloAdapter = new ArticuloAdapter (getActivity(), articulos);
        lvCatalogo.setAdapter(ArticuloAdapter);



    /**************************************************************************************************/
    /* Evento touch / click de un elemento del ListView de catálogo*/
        lvCatalogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Creamos un TextView para recuperar el id del producto*/
                final TextView tvIdArticulo = view.findViewById(R.id.tv_idArticulo);
                final String ArticuloId = tvIdArticulo.getText().toString();

                /*Enviar el idArticulo a ProductoActivity por medio de un Extra*/

                /*Lanzar el activity Detalle para mostrar el detalle de cada producto*/
                startActivity(
                        new Intent(
                                getContext(),
                                ProductoActivity.class
                        )
                                .putExtra("idArticulo", ArticuloId)
                );
            }
        });


    /**************************************************************************************************/
        return fragmentView;
    } //ONCREATE

    /*Metodo para cargar películas desde un Servicio usando Volley*/
    public void cargaProductos() {
        petServidor = new StringRequest(
                Request.Method.POST,

                BASE_URL+ "articulo/getarticulos",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* Si la respuesta es correcta verificamos que existan productos */
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            /* Si la respuesta es un objeto con productos*/
                            if (respuesta.getInt("respuesta") == 200) {
                                /* Generar un arreglo de productos */
                                JSONArray productos = respuesta.getJSONArray("articulos");

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

                                /* Actualizacion grafica del catalogo*/
                                ArticuloAdapter = new ArticuloAdapter(
                                        getActivity(),
                                        articulos
                                );
                                lvCatalogo.setAdapter(ArticuloAdapter);
                                ArticuloAdapter.notifyDataSetChanged();

                                srlCatalogo.setRefreshing(false);
                            }
                        }
                        catch(Exception e) {
                            Toast.makeText(getActivity(), //Context para fragmentos
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            srlCatalogo.setRefreshing(false);
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
        );
        //Ejecutamos el servicio
        conServidor.add(petServidor);
    }
}
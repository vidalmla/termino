package com.example.sensacion.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sensacion.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/* BaseAdapter <<clase Abstracta>> */

public class ArticuloAdapter extends BaseAdapter {

    /* Los atributos  para generar catalogo */
    private Context contexto;
    private List<Articulo> articulos;
    private LayoutInflater inflater;

    /* Constructor que inicializa los elementos anteriores*/
    public ArticuloAdapter(Context contexto, List articulos) {
        this.contexto = contexto;
        this.articulos = articulos;
        inflater = LayoutInflater.from(contexto);
    }

    /* Numero de lementos que tiene nuestro catalogo*/
    @Override
    public int getCount() {
        return articulos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /* Indicamos el diseño que se aplicará POR CADA ELEMENTO */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* El diseño sera el mismo para todos los elementos del catalogo */
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_producto, null);
        }

        /*  Vinculamos los elementos de la vista con variables locales */
        ImageView ivImagen   = convertView.findViewById(R.id.iv_imagen);
        TextView tvIdArticulo = convertView.findViewById(R.id.tv_idArticulo);
        TextView tvNombre  = convertView.findViewById(R.id.tv_nombre);
        TextView tvCategoria = convertView.findViewById(R.id.tv_categoria);
        TextView tvPrecio = convertView.findViewById(R.id.tv_precio);

        /* Mostramos cada elemento de la lista en nuestro layout utilizando la variable posotion */
        Picasso.get().load(articulos.get(position).getImagen()).into(ivImagen);
        tvIdArticulo.setText(String.valueOf(articulos.get(position).getIdArticulo()));
        tvNombre.setText(articulos.get(position).getNombre());
        tvCategoria.setText(articulos.get(position).getCategoria());
        tvPrecio.setText(String.valueOf(articulos.get(position).getPrecio()));

        /* Mostramos la vista */
        return convertView;
    }
}

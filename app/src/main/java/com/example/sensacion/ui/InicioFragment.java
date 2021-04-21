package com.example.sensacion.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sensacion.R;




public class InicioFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Referencia al fragmento
        View fragmentView = inflater.inflate(R.layout.fragment_inicio, container, false);


        return fragmentView;
    }/*ONCREATE*/

}/*CLASE*/
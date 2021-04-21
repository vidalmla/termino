package com.example.sensacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AlertDialog.Builder alerta;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alerta = new AlertDialog.Builder(MainActivity.this);

        /*Inicializamos la referencia al archivo SP*/
        prefs       = getSharedPreferences("sensacion_datos", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio,
                R.id.nav_cuenta,
                R.id.nav_catalogo,
                R.id.nav_listadeseos,
                R.id.nav_carritocompras,
                R.id.nav_comprar
                )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /*Programamos el click del cada elemento del menu izquierdo*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*Evaluar cual de todos los menus fue el seleccionado*/
        switch (item.getItemId()){
            //Si el menu seleccionado es salir
            case R.id.m_salir:
                Salir();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Salir();

    }

    /*Método que muestra la alerta para salir*/

    public void Salir(){
        alerta.setTitle("Cerrar sesión")
                .setMessage("¿Realmente deseas salir?")
                .setIcon(R.drawable.info)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        Elimnar los registros almacenados de este
                        usuario en archivo SharedPreferences

                        Forma 1 : Elimnar un valor específico del workspace
                        prefsEditor.remove("id");
                        prefsEditor.remove("correo");

                        Forma 2 : Eliminar TODOS LOS VALORES del workspace
                        */
                        prefsEditor.clear();
                        prefsEditor.commit();

                        startActivity(new Intent(
                                MainActivity.this,
                                LoginActivity.class
                        ));
                    }
                })
                .setCancelable(false)
                .show();

    }
}
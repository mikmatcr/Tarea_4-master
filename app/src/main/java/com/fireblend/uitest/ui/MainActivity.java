package com.fireblend.uitest.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblend.uitest.AddContact;
import com.fireblend.uitest.Preferencias;
import com.fireblend.uitest.R;
import com.fireblend.uitest.bd.Contactosbd;
import com.fireblend.uitest.bd.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {


    RecyclerView list;
    ArrayList<Contactosbd> datos;
    Button addcontac;
    AdaptadorContactos adaptador;
    DatabaseHelper helper;

    Button preferencias;
    TextView name;
    TextView age;
    TextView phone;
    TextView email;
    TextView provincia;



    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView)findViewById(R.id.name);
        age = (TextView)findViewById(R.id.age);
        phone = (TextView)findViewById(R.id.phone);
        email = (TextView)findViewById(R.id.email);
        provincia= (TextView)findViewById(R.id.provincia);



        list = (RecyclerView) findViewById(R.id.lista_contactos);


        list.setHasFixedSize(true);
        //Dar estructura al RecyclerView
        list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        try {
            mostrar();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


        adaptador = new AdaptadorContactos(datos);


        //Mostrar mensaje al tocar un dato
        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = list.getChildAdapterPosition(view);
                if(pos != RecyclerView.NO_POSITION){
                    Contactosbd clickedDataItem = datos.get(pos);
                    Toast.makeText(view.getContext(), "Hola " + clickedDataItem.nombre, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    String ID = Integer.toString(clickedDataItem.id);
                    String mensaje = ID;

                    intent.putExtra("ID", mensaje);


                    startActivity(intent);

                }

            }
        });

        list.setAdapter(adaptador);



        //Ir al nuevo Activity
        addcontac = (Button)findViewById(R.id.add_contact);
        addcontac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContact.class);

                startActivity(intent);
            }
        });

        //Ir a preferencias
        preferencias = (Button)findViewById(R.id.preferencias);
        preferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Preferencias.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {

        cargarpreferencias();

        super.onResume();
        try {
            mostrar();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }


    //Metodo para cargar preferencias
    private void cargarpreferencias() {

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String lista = sharedPrefs.getString("lista","No hay");

        switch (lista){
            case "Lista":
                list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            break;
            case "Columna":

                list.setLayoutManager(new GridLayoutManager(this,2));
                break;
        }


    }


    private void cargardatos() throws java.sql.SQLException {
        DatabaseHelper helper = new DatabaseHelper(this);
        Dao<Contactosbd, Integer> usuarioDao = null;
        try {
            usuarioDao = helper.getUsuarioDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Contactosbd contacto = new Contactosbd();
        contacto.nombre = "Sergio2";
        contacto.edad = 12;
        contacto.correo = "miiiii@gmail.com";
        contacto.telefono = "123456";
        contacto.provincia = "San";
        try {
            usuarioDao.create(contacto);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void mostrar() throws java.sql.SQLException {

        DatabaseHelper helper = new DatabaseHelper(this);
        Dao<Contactosbd, Integer> usuarioDao = null;
        try {
            usuarioDao = helper.getUsuarioDao();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        //Generamos un filtro y obtenemos la lista resultado
        //List<Contactosbd> datos = null;
        try {
            List<Contactosbd> contacto = usuarioDao.queryForAll();
            datos = new ArrayList<Contactosbd>(contacto);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

}

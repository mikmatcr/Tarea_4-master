package com.fireblend.uitest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblend.uitest.bd.Contactosbd;
import com.fireblend.uitest.bd.DatabaseHelper;
import com.fireblend.uitest.ui.MainActivity;
import com.j256.ormlite.dao.Dao;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContact extends AppCompatActivity {

    @BindView(R.id.boton_registro)
    Button boton_registro;

    @BindView(R.id.TxtNombre)
    EditText TxtNombre;

    @BindView(R.id.TxtEdad)
    EditText TxtEdad;

    @BindView(R.id.TxtTelefono)
    EditText TxtTelefono;

    @BindView((R.id.TxtEmail))
    EditText TxtEmail;

    @BindView((R.id.provincias))
    Spinner provincias;

    @BindView(R.id.txt_provincia)
    TextView txt_provincia;

    String provincia = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);

        final String[] prov = new String[]{"San José","Alajuela","Puntarenas","Guanacaste","Limón","Heredia","Cartago"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, prov);//Forma de mostrar el array
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//Saldrán las opciones hacía abajo
        provincias.setAdapter(adaptador);

        provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              provincia = (adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }



        @OnClick(R.id.boton_registro)
               public void boton(){
            String nombre = TxtNombre.getText().toString();
            String edad = TxtEdad.getText().toString();
            String telefono = TxtTelefono.getText().toString();
            String email = TxtEmail.getText().toString();
            //String provincia = provincia.getText().toString();

            //Validamos que no queden espacios sin llenar
            if(nombre.isEmpty()|edad.isEmpty()|telefono.isEmpty()|email.isEmpty()){
                Toast.makeText(AddContact.this,"Debe llenar todos los campos",Toast.LENGTH_LONG).show();
                return;
            }
            else
                {
                    //Creamos el Intent
                Integer edad1 = Integer.parseInt(edad);
                Intent myIntent = new Intent(AddContact.this, MainActivity.class);


                guardardatosorm(nombre,email,telefono,provincia,edad1);


                startActivity(myIntent);
            }

        }



        private void guardardatosorm(String nombre,String correo, String telefono, String provincia, Integer edad){
            DatabaseHelper helper = new DatabaseHelper(this);
            Dao<Contactosbd, Integer> usuarioDao = null;
            try {
                usuarioDao = helper.getUsuarioDao();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }


            Contactosbd contacto = new Contactosbd();
            contacto.nombre = nombre;
            contacto.edad = edad;
            contacto.correo = correo;
            contacto.telefono = telefono;
            contacto.provincia = provincia;
            try {
                usuarioDao.create(contacto);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Se cargó el nuevo contacto",
                    Toast.LENGTH_SHORT).show();
        }
}

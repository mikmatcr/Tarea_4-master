package com.fireblend.uitest.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblend.uitest.R;
import com.fireblend.uitest.bd.Contactosbd;
import com.fireblend.uitest.bd.DatabaseHelper;
import com.fireblend.uitest.entities.GestorContactos;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    GestorContactos datos;

    public DetailsFragment() {
        // Required empty public constructor
    }

    TextView TxtNombre;
    TextView TxtEdad;
    TextView TxtTelefono;
    TextView TxtEmail;
    TextView provincias;
    Button boton_eliminar;
    Button boton_exportar;
    RelativeLayout fondo_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        TxtNombre = (TextView)view.findViewById(R.id.TxtNombre);
        TxtEdad = (TextView)view.findViewById(R.id.TxtEdad);
        TxtEmail = (TextView)view.findViewById(R.id.TxtEmail);
        TxtTelefono= (TextView)view.findViewById(R.id.TxtTelefono);
        provincias = (TextView)view.findViewById(R.id.provincias);

        try {
            mostrar();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        boton_eliminar = (Button)view.findViewById(R.id.boton_eliminar);
        fondo_layout =(RelativeLayout) view.findViewById(R.id.layout_contact);
        cargarpreferencias();
        boton_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar();

            }
        });

        boton_exportar = (Button)view.findViewById(R.id.boton_exportar);
        boton_exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Revisar si se tiene Permiso:
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                    try {
                        continuar();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    askForPermission();
                }

            }
        });

        return view;
    }

    private void cargarpreferencias() {

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());


        boolean quitar_borrar = sharedPrefs.getBoolean("quitar_borrar", false);
        String prueba = "";
        if (quitar_borrar == false){
            prueba = "Falso";

        }else{
            prueba = "Si";
            boton_eliminar.setVisibility(View.INVISIBLE);
        }
        String fondo = sharedPrefs.getString("fondo", "No hay");
        switch(fondo){
            case "Red":
                fondo_layout.setBackgroundColor(Color.RED);
                break;
            case "Blue":
                fondo_layout.setBackgroundColor(Color.BLUE);
                break;
            case "White": fondo_layout.setBackgroundColor(Color.WHITE);
                break;

        }

    }

    //Creando y Accediendo a Archivos:
    private void continuar() throws IOException {
        Toast.makeText(getActivity(), "Tenemos Permiso!", Toast.LENGTH_SHORT).show();
        //Creo la carpeta
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "Prueba");

        if (!dir.mkdirs()) {
            Log.e(TAG, "Directory not created");

        }
        String archivo = "PruebaMike.txt"; //nombre del archivo
        //Creación del archivo
        File file = new File(dir,archivo);
        file.createNewFile();
        llenararchivo(archivo, file);
    }

    private  void llenararchivo(String Filename, File file){
        //traemos los datos nuevos del Activity AddContact
        String mensaje = getActivity().getIntent().getStringExtra("ID");
        int numMensaje = Integer.parseInt(mensaje);
        DatabaseHelper helper = new DatabaseHelper(getActivity());
        Dao<Contactosbd, Integer> usuarioDao = null;
        try {
            usuarioDao = helper.getUsuarioDao();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


        try {

            Contactosbd contactos = usuarioDao.queryForId(numMensaje);

            TxtNombre.setText(contactos.nombre);
            String name = TxtNombre.getText().toString();
            TxtEdad.setText(String.valueOf(contactos.edad));
            String edad = TxtEdad.getText().toString();
            TxtEmail.setText(contactos.correo);
            String correo = TxtEmail.getText().toString();
            TxtTelefono.setText(contactos.telefono);
            String telefono = TxtTelefono.getText().toString();
            provincias.setText(contactos.provincia);
            String provincia = provincias.getText().toString();

            //String Filename = "PruebaMike.txt";
            String campos = name + "," + edad + "," + correo + "," + telefono + "," + provincia;

            OutputStreamWriter bf = new OutputStreamWriter(new FileOutputStream(file));
            bf.write(campos);
            bf.close();




        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Mostramos los datos del contacto
    public void mostrar() throws java.sql.SQLException {
        //traemos los datos nuevos del Activity AddContact
        String mensaje = getActivity().getIntent().getStringExtra("ID");
        int numMensaje = Integer.parseInt(mensaje);
        DatabaseHelper helper = new DatabaseHelper(getActivity());
        Dao<Contactosbd, Integer> usuarioDao = null;
        try {
            usuarioDao = helper.getUsuarioDao();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


        try {

            Contactosbd contactos = usuarioDao.queryForId(numMensaje);

            TxtNombre.setText(contactos.nombre);
            TxtEdad.setText(String.valueOf(contactos.edad));
            TxtEmail.setText(contactos.correo);
            TxtTelefono.setText(contactos.telefono);
            provincias.setText(contactos.provincia);



        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminar(){
        final DatabaseHelper helper = new DatabaseHelper(getActivity());
        //traemos los datos nuevos del Activity AddContact
        String mensaje = getActivity().getIntent().getStringExtra("ID");
        int numMensaje = Integer.parseInt(mensaje);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Está seguro de borrar el contacto?")
                .setTitle("Confirmacion")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //traemos los datos nuevos del Activity AddContact
                        String mensaje = getActivity().getIntent().getStringExtra("ID");
                        int numMensaje = Integer.parseInt(mensaje);

                        Dao<Contactosbd, Integer> usuarioDao = null;
                        try {
                            usuarioDao = helper.getUsuarioDao();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            e.printStackTrace();
                        }


                        Contactosbd contacto = new Contactosbd();
                        contacto.id = numMensaje;
                        try {
                            usuarioDao.delete(contacto);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.cancel();

                        Toast.makeText(getActivity(), "Se borró el contacto",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Cancelada.");
                        dialog.cancel();
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();

    }

    private static final int PERM_CODE = 100;
    public void askForPermission(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERM_CODE);
    }

    //Manejando la Respuesta del Usuario:

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            try {
                continuar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    //Verificación de Disponibilidad para Escritura:
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //Verificación de Disponibilidad para Lectura:
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

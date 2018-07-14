package com.fireblend.uitest.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fireblend.uitest.R;
import com.fireblend.uitest.entities.GestorContactos;

public class DetailsActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager fm;
    DetailsFragment detailsFragment = new DetailsFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        
        fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.contenedor1, detailsFragment);
        ft.commit();
    }


}

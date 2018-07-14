package com.fireblend.uitest.entities;

import android.content.Context;

import com.fireblend.uitest.bd.DatabaseHelper;

public final class GestorContactos {

    private static DatabaseHelper baseDatos;

    private static GestorContactos instancia = new GestorContactos();

    private GestorContactos() {
    }

    public static GestorContactos obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new DatabaseHelper(contexto);
        }
        return instancia;
    }
}

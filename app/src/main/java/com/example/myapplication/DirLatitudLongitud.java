package com.example.myapplication;

import java.text.DecimalFormat;

public class DirLatitudLongitud{
    private static Double latitud;
    private static Double longitud;

    public static void setLatitud(Double latitud) {
        DirLatitudLongitud.latitud = latitud;
    }

    public static void setLongitud(Double longitud) {
        DirLatitudLongitud.longitud = longitud;
    }

    public static String getLocalizacion() {
        DecimalFormat df = new DecimalFormat("###.######");
        return "("+ latitud + ", " + longitud +")";
    }

    public static Double getLatitud() {
        return latitud;
    }

    public static Double getLongitud() {
        return longitud;
    }
}


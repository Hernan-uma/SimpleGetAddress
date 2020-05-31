package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

@SuppressLint("Registered")
public class Localizador extends AppCompatActivity {
    private static final String TAG = Localizador.class.getSimpleName();
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;


    public Localizador(Context context) {
        this.context = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity)context);
        getLocalizacionActual();
    }

    public void reiniciarLocalizacion(){
        Log.i(TAG, "Reiniciando...");
        getLocalizacionActual();
    }


    /*
    Sirve para responder en a los resultados de haber pedido permiso para usar el gps
    Si se otorga, se prosigue con la obtencion de la localizacion y si no
    se muestra un texto donde se especifica que es necesario el permiso para la app
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocalizacionActual();
            }
        }
    }

    /**
     * Primero hacemos un set de todos los valores necesarios para la request de la Localizacion
     * luego utilizamos el método Request Location Updates en el que sobreescribiremos un método
     * una vez hecho esto conseguiremos la latitud y longitud en grados decimales
     */
    private void getLocalizacionActual() {
        if (comprobarPermisos()) {
            Log.i(TAG, "Permisos correctos");
            if (comprobarLocalizacionActiva()) {
                requestNewLocationData();
            } else {
                Toast.makeText(this, "Es necesario tener la localización activa", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            obtenerPermisos();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity) context);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            DirLatitudLongitud.setLatitud(latitude);
            DirLatitudLongitud.setLongitud(longitude);
            Log.i(TAG,"Obtenido: " + DirLatitudLongitud.getLocalizacion());


        }
    };

    private void obtenerPermisos() {
        Log.i(TAG, "Pidiendo permisos para localización...");
        ActivityCompat.requestPermissions(
                (Activity)context,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.PERMISSION_ID
        );
    }

    private boolean comprobarPermisos() {
        Log.i(TAG,"Comprobando permisos...");
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean comprobarLocalizacionActiva() {
        Log.i(TAG,"Comprobando localización activada...");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
}
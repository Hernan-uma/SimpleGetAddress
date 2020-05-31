package com.example.myapplication;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView latLong, address;
    private Button reinicio;

    private  ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultReceiver = new AddressResultReceiver(new Handler());

        latLong = findViewById(R.id.txt_LatLong);
        address = findViewById(R.id.txt_Address);
        reinicio = findViewById(R.id.btn_reiniciar);

        Log.i(TAG, "Se inicia MainActivity");
        final Localizador localizador = new Localizador(MainActivity.this);

        reinicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localizador.reiniciarLocalizacion();
                latLong.setText(DirLatitudLongitud.getLocalizacion());

                Location location = new Location("providerNA");
                location.setLatitude(DirLatitudLongitud.getLatitud());
                location.setLongitude(DirLatitudLongitud.getLongitud());

                Log.i(TAG, "Se crea una location");
                fetchAddressFromLatLong(location);
            }
        });

    }

    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(this, FetchAddressIntentServices.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                // Publicando resultados
                address.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                Log.i(TAG,"Se debería publicar el resultado");
            } else {
                Toast.makeText(MainActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }

        }
    }
}

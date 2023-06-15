package com.example.animalsdelllano;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView connectionStatusTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionStatusTextView = findViewById(R.id.estadointernet);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final boolean isConnected = isInternetConnected();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isConnected) {
                                connectionStatusTextView.setText("Conectado");
                            } else {
                                connectionStatusTextView.setText("Sin Conexión");
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000); // wait for 1 second
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
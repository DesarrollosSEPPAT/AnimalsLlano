package com.example.animalsdelllano;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ResumenPedido extends AnularBotonRetroceso {

    String CodigoPedido;
    private TextView NombreUsu;
    ArrayList<String> DatosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedido);

        NombreUsu = findViewById(R.id.nombreUser);

        Intent intent = getIntent();
        CodigoPedido = intent.getStringExtra("CodigoPedido");
        DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);



    }
}
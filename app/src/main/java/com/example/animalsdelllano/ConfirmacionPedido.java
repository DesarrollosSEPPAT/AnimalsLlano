package com.example.animalsdelllano;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ConfirmacionPedido extends AnularBotonRetroceso {

    private TextView NombreUsu;
    String CodigoPedido;
    private TextView CodPedido;
    private ImageButton menuvendedor;

    ArrayList<String> DatosUsuario = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_pedido);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        NombreUsu = findViewById(R.id.nombreUser);
        CodPedido = findViewById(R.id.codped);

        // Recibir el codigo del pedido
        Intent intent = getIntent();
        CodigoPedido = intent.getStringExtra("CodigoPedido");
        DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);
        CodPedido.setText("El código del pedido es: " + CodigoPedido);

        // Boton volver al menu principal
        menuvendedor = findViewById(R.id.menuppalvendedor);
        menuvendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmacionPedido.this, Vendedor.class);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();
            }
        });

    }

}
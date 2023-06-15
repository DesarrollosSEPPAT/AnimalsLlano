package com.example.animalsdelllano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

public class Vendedor extends AnularBotonRetroceso {

    private ImageButton CrearPedido;
    private ImageButton VerEditarPedido;
    private ImageButton AnularPedido;
    private TextView NombreUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);

        NombreUsu = findViewById(R.id.nombreUser);

        Intent intent = getIntent();
        ArrayList<String> DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);

        CrearPedido = findViewById(R.id.CrearPedido);
        CrearPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Cuando el vendedor vaya a crear un pedido se genera un código automáticamente
                UUID uuid = UUID.randomUUID();
                String CodigoPedido = uuid.toString().substring(0,8);

                Intent intent = new Intent(Vendedor.this, RelacionarCliente.class);
                intent.putExtra("CodigoPedido", CodigoPedido);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();

            }
        });

        VerEditarPedido = findViewById(R.id.ModificarPedido);
        VerEditarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Vendedor.this, ListaPedidos.class);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();

            }
        });

        AnularPedido= findViewById(R.id.AnularPedido);
        AnularPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Vendedor.this, EliminarPedidos.class);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();

            }
        });



    }
}
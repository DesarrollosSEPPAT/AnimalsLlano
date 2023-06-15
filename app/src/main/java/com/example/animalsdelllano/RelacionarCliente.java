package com.example.animalsdelllano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelacionarCliente extends AnularBotonRetroceso {

    // Codigo del pedido
    String CodigoPedido;
    private ImageButton retroceso;

    // Gestor de ejecucion cada cierto tiempo
    private Handler handler = new Handler();
    private Runnable runnable;

    // Array que recibe las posibles opciones del cliente, sucursal y toda su informacion
    List<String> NombreCliente = new ArrayList<String>();
    List<String> SucursalCliente = new ArrayList<String>();
    ArrayList<HashMap<String, String>> ClienteCompleto = new ArrayList<>();
    ArrayList<String> ClienteC = new ArrayList<>();

    // Crear las variables de las variables y los campos de texto
    private TextView NombreUsu;

    // Crear la instancia de la clase Bases de Datos
    BasesDeDatos dbCl = new BasesDeDatos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relacionar_cliente);

        // Relacionar la variable local con la variable del layout
        NombreUsu = findViewById(R.id.nombreUser);

        // Recibir el codigo del pedido y el nombre de usuario
        Intent intent = getIntent();
        CodigoPedido = intent.getStringExtra("CodigoPedido");
        ArrayList<String> DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);

        // Colocar las opciones del Tipo de producto
        AutoCompleteTextView ClienteAC = findViewById(R.id.cliente);
        AutoCompleteTextView SucursalAC = findViewById(R.id.sucursal);
        TextView NITTW = findViewById(R.id.identificacion);
        TextView DireccionTW = findViewById(R.id.direccion);
        TextView CiudadTW = findViewById(R.id.ciudad);
        TextView TelefonoTW = findViewById(R.id.telefono);

        // De inicio esta deshabilitado los botones. Solo se habilitan cuando el contenido del texto anterior sea valido
        SucursalAC.setEnabled(false);

        // Configurar parametros visuales
        Toast.makeText(getApplicationContext(), "Para relacionar un cliente, diligencia los campos en el orden presentado", Toast.LENGTH_LONG).show();

        // Obtener los valores unicos del tipo de producto
        NombreCliente = dbCl.nombresClientes("Cliente");
        // Ajustar el edittext de producto
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, NombreCliente);
        ClienteAC.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {

                String ClienteS = ClienteAC.getText().toString();
                String SucursalS = SucursalAC.getText().toString();

                String Dir;
                String Tel;
                String CC;
                String City;

                // Realiza la acción después de 1/2 segundo
                try {
                    Visuales(ClienteAC, SucursalAC);

                    // Ajustes del EditText de producto
                    ClienteAT(ClienteAC,SucursalAC,adapter);
                    ClienteCompleto = dbCl.ClienteCompleto(ClienteS, SucursalS);

                    // Colocar el valor del arraylist en cada objeto visual
                    CC = ClienteCompleto.get(0).get("IDCliente");
                    Dir = ClienteCompleto.get(0).get("Direccion");
                    Tel = ClienteCompleto.get(0).get("Telefono");
                    City = ClienteCompleto.get(0).get("Ciudad");
                    NITTW.setText(CC);
                    DireccionTW.setText(Dir);
                    TelefonoTW.setText(Tel);
                    CiudadTW.setText(City);

                } catch (Exception e) {
                    ClienteCompleto.clear();
                    NITTW.setText("");
                    DireccionTW.setText("");
                    TelefonoTW.setText("");
                    CiudadTW.setText("");
                }
                handler.postDelayed(this, 500);
            }
        };
        // Iniciamos la actualización
        handler.postDelayed(runnable, 500);

        // Funciones del boton siguiente
        Button Siguiente = findViewById(R.id.ConfirmarCliente);
        Siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // No puedo avanzar si alguno de los campos obligatorios no esta diligenciado
                if (ClienteAC.getText().toString().equals("") || SucursalAC.getText().toString().equals("")){
                    Toast.makeText(RelacionarCliente.this, "Debes diligenciar toda la información obligatoria. Revisa de nuevo todos los campos.", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(RelacionarCliente.this, AgregarProductos.class);
                    intent.putExtra("CodigoPedido", CodigoPedido);
                    intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                    ClienteC.add(ClienteCompleto.get(0).get("IDCliente"));
                    ClienteC.add(ClienteAC.getText().toString());
                    ClienteC.add(SucursalAC.getText().toString());
                    ClienteC.add(ClienteCompleto.get(0).get("Direccion"));
                    ClienteC.add(ClienteCompleto.get(0).get("Ciudad"));
                    ClienteC.add(ClienteCompleto.get(0).get("Telefono"));
                    intent.putStringArrayListExtra("InformacionCliente", ClienteC);
                    startActivity(intent);
                    finish();

                }

            }
        });

        // Funcionalidad del boton de retroceso
        retroceso = findViewById(R.id.retroceder);
        retroceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RelacionarCliente.this, Vendedor.class);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();
            }
        });

    }

    // Funciones que se ejecutaran sobre y desde el EditText del Cliente
    public void ClienteAT(AutoCompleteTextView ClienteAT, AutoCompleteTextView SucursalAT, ArrayAdapter<String> AdaptadorCliente){

        // Funciones visuales
        ClienteVisual(ClienteAT, AdaptadorCliente);

        // Funciones funcionales
        ClienteFuncional(ClienteAT, AdaptadorCliente, SucursalAT);

    }
    public void ClienteVisual(AutoCompleteTextView ClienteAT, ArrayAdapter<String> AdaptadorCliente) {

        // Validar que solo se puedan colocar las opciones del adaptador
        ClienteAT.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if (AdaptadorCliente.getPosition(text.toString()) >= 0) {
                    // El valor es válido porque está en el ArrayAdapter
                    return true;
                } else {
                    // El valor no es válido
                    return false;
                }
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                // No es necesario corregir el texto
                return "";
            }
        });

    }
    public void ClienteFuncional(AutoCompleteTextView ClienteAT, ArrayAdapter<String> AdaptadorCliente, AutoCompleteTextView SucursalAT) {

        ClienteAT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence tipo, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cliente, int start, int before, int count) {

                // Buscar en la base de datos las posibles opciones del tipo escogido
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (AdaptadorCliente.getPosition(cliente.toString()) >= 0) {
                            // El valor es válido porque está en el ArrayAdapter
                            // Si es valido habilitar el edittext, si no, deshabilitarlo
                            SucursalAT.setEnabled(true);
                        } else {
                            // El valor no es válido
                            SucursalAT.setEnabled(false);
                        }

                        // Realiza la acción después de 1/2 segundo
                        try {
                            SucursalCliente = dbCl.SucursalesUnicas(cliente);
                        } catch (Exception e) {
                            SucursalCliente.clear();
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapterSucursal = new ArrayAdapter<>(RelacionarCliente.this, android.R.layout.simple_dropdown_item_1line, SucursalCliente);
                        SucursalAT.setAdapter(adapterSucursal);

                        // Funciones para ajustar el funcionamiento del EditText de Marca
                        SucursalVisual(SucursalAT, adapterSucursal);
                    }
                }, 300);

            }

            @Override
            public void afterTextChanged(Editable tipo) {

            }
        });

    }
    public void SucursalVisual(AutoCompleteTextView SucursalAT, ArrayAdapter<String> adaptadorSucursal) {

        // Validar que solo se puedan colocar las opciones del adaptador
        SucursalAT.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if (adaptadorSucursal.getPosition(text.toString()) >= 0) {
                    // El valor es válido porque está en el ArrayAdapter
                    return true;
                } else {
                    // El valor no es válido
                    return false;
                }
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                // No es necesario corregir el texto
                return "";
            }
        });
    }

    // Solo ajustar y configurar los ajustes visuales
    public void Visuales(AutoCompleteTextView ClienteAT, AutoCompleteTextView SucursalAT){

        // Desplegar la lista de opciones cuando se toca el campo de texto
        ClienteAT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ClienteAT.showDropDown();
                }
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        ClienteAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteAT.showDropDown();
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        SucursalAT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SucursalAT.showDropDown();
                }
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        SucursalAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SucursalAT.showDropDown();
            }
        });

    }

}
package com.example.animalsdelllano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ModificarProductos extends AnularBotonRetroceso {

    // Codigo del pedido
    String CodigoPedido;

    private TextView NombreUsu;

    String IDCliente;
    String Cliente;
    String Sucursal;
    String Direccion;
    String Telefono;
    String Referencia;
    String Tipo;
    String Marca;
    String Nombre;
    String Presentacion;
    String Descripcion;
    String ValorUnitario;
    String Cantidad;
    String Descuento;
    String CantidadAdicional;
    String Observaciones;

    ArrayList<String> DatosUsuario = new ArrayList<>();
    ArrayList<String> ProductoAL = new ArrayList<>();
    ArrayList<String> DatosCliente = new ArrayList<>();
    ArrayList<String> ClienteC = new ArrayList<>();

    // Crear la instancia de la clase Bases de Datos
    BasesDeDatos dbPL = new BasesDeDatos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_productos);

        NombreUsu = findViewById(R.id.nombreUser);

        // Recibir el codigo del pedido, datos de usuario
        Intent intent = getIntent();
        CodigoPedido = intent.getStringExtra("CodigoPedido");
        DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);

        // Recibir los datos del cliente
        DatosCliente = intent.getStringArrayListExtra("InformacionCliente");
        IDCliente = DatosCliente.get(0);
        Cliente = DatosCliente.get(1);
        Sucursal = DatosCliente.get(2);
        Direccion = DatosCliente.get(3);
        Telefono = DatosCliente.get(4);

        ClienteC.add(IDCliente);
        ClienteC.add(Cliente);
        ClienteC.add(Sucursal);
        ClienteC.add(Direccion);
        ClienteC.add(Telefono);

        // Datos del producto
        ProductoAL = intent.getStringArrayListExtra("InformacionProducto");

        Referencia = ProductoAL.get(0);
        Tipo = ProductoAL.get(1);
        Marca = ProductoAL.get(2);
        Nombre = ProductoAL.get(3);
        Presentacion = ProductoAL.get(4);
        Descripcion = ProductoAL.get(5);
        ValorUnitario = ProductoAL.get(6);
        Cantidad = ProductoAL.get(7);
        Descuento = ProductoAL.get(8);
        CantidadAdicional = ProductoAL.get(9);
        Observaciones = ProductoAL.get(10);

        // Relacionar los objetos visuales
        TextView TipoAC = findViewById(R.id.tipoProducto);
        TextView MarcaAC = findViewById(R.id.Marca);
        TextView NombreAC = findViewById(R.id.Nombre);
        TextView PresentacionAC = findViewById(R.id.Presentacion);

        TextView ReferenciaTW = findViewById(R.id.Referencia);
        TextView DescripcionTW = findViewById(R.id.Descripcion);
        TextView ValorUnitarioTW = findViewById(R.id.valorUnitario);

        EditText CantidadET = findViewById(R.id.Cantidades);
        EditText DescuentoET = findViewById(R.id.descuento);
        EditText CantidadDescuentoET = findViewById(R.id.CantidadesDescuento);
        EditText ComentariosET = findViewById(R.id.Comentarios);

        // Pegar los valores del array
        TipoAC.setText(Tipo);
        MarcaAC.setText(Marca);
        NombreAC.setText(Nombre);
        PresentacionAC.setText(Presentacion);
        ReferenciaTW.setText(Referencia);
        DescripcionTW.setText(Descripcion);
        ValorUnitarioTW.setText(NumeroAjustado(ValorUnitario));
        CantidadET.setText(Cantidad);
        DescuentoET.setText(Descuento);
        CantidadDescuentoET.setText(CantidadAdicional);
        ComentariosET.setText(Observaciones);

        // Agregar los botones y sus funcionalidades
        Button ModificarItem = findViewById(R.id.modificaritem);
        ModificarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // No puedo agregar un item si alguno de los campos obligatorios no esta diligenciado
                if (TipoAC.getText().toString().equals("") || MarcaAC.getText().toString().equals("") || NombreAC.getText().toString().equals("") || PresentacionAC.getText().toString().equals("") || CantidadET.getText().toString().equals("")){
                    Toast.makeText(ModificarProductos.this, "Debes diligenciar toda la información obligatoria. Revisa de nuevo todos los campos.", Toast.LENGTH_SHORT).show();
                } else {
                    // Leer y validar el descuento y las cantidades adicionales, pues estos datos no son obligatorios
                    int DescuentoS;
                    if (DescuentoET.getText().toString().equals("")){
                        DescuentoS = 0;
                    } else {
                        DescuentoS = Integer.valueOf(DescuentoET.getText().toString());
                    }

                    int CantidadDescuentoS;
                    if (CantidadDescuentoET.getText().toString().equals("")) {
                        CantidadDescuentoS = 0;
                    } else {
                        CantidadDescuentoS = Integer.valueOf(CantidadDescuentoET.getText().toString());
                    }

                    // Leer todos los datos ingresados en la interfaz, asi como el codigo del pedido
                    String ReferenciaS = ReferenciaTW.getText().toString();
                    String TipoS = TipoAC.getText().toString();
                    String MarcaS = MarcaAC.getText().toString();
                    String NombreS = NombreAC.getText().toString();
                    String PresentacionS = PresentacionAC.getText().toString();
                    String DescripcionS = DescripcionTW.getText().toString();
                    int ValorUnitarioS = Integer.valueOf(ValorUnitario);
                    int CantidadN = Integer.valueOf(CantidadET.getText().toString());
                    String ObservacionesS = ComentariosET.getText().toString();

                    ////// POR VALIDAR COMO SE APLICAN LOS DESCUENTOS
                    int ValorTotal = (ValorUnitarioS * (1 - (DescuentoS / 100))) * CantidadN;

                    // El estatus solo puede ser cambiado por la persona que alista el pedido
                    int Estatus = 0;

                    // Modificar el pedido
                    dbPL.ModificarProductoLocal(CodigoPedido, ReferenciaS, TipoS, MarcaS, NombreS, PresentacionS, DescripcionS, ValorUnitarioS, CantidadN, DescuentoS, CantidadDescuentoS, ValorTotal, ObservacionesS, DatosUsuario.get(2), Estatus);

                    // Mensaje de confirmacion
                    Toast.makeText(ModificarProductos.this, "Item modificado con exito", Toast.LENGTH_SHORT).show();

                    // Saltar a la ventana de la tabla
                    Intent intent = new Intent(ModificarProductos.this, ResumenProductos.class);
                    intent.putExtra("CodigoPedido", CodigoPedido);
                    intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                    intent.putStringArrayListExtra("InformacionCliente", ClienteC);
                    startActivity(intent);
                    finish();


                }

            }
        });

    }

    public String NumeroAjustado(String Precio){

        // Convertir el precio a formato double
        double precio = Double.valueOf(Precio);

        Locale locale = new Locale("es", "CO"); // crea un Locale para el formato deseado
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale); // crea una instancia de NumberFormat para el formato de moneda
        nf.setMaximumFractionDigits(0);
        String formattedNumber = nf.format(precio); // formatea el número como una cadena con el formato de moneda

        return formattedNumber;

    }
}
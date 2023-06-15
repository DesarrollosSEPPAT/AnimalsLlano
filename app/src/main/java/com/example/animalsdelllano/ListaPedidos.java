package com.example.animalsdelllano;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListaPedidos extends AnularBotonRetroceso {

    private TextView NombreUsu;
    private TableLayout mTabla;
    private ImageButton retroceso;
    ArrayList<String> DatosUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);

        NombreUsu = findViewById(R.id.nombreUser);

        Intent intent = getIntent();
        DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);

        retroceso = findViewById(R.id.retroceder);
        mTabla = findViewById(R.id.Pedidos);

        // Dibujar tabla
        TablaPedidos(DatosUsuario.get(0));

        // Funcionalidad del boton de retroceso
        retroceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaPedidos.this, Vendedor.class);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();
            }
        });

    }

    public void TablaPedidos(String CodigoVendedor){

        // Conectar a la base de datos
        SQLiteDatabase db = null;
        Cursor cursor = null;

        // Abrir la base de datos en modo lectura
        db = SQLiteDatabase.openDatabase(getDatabasePath("ProductosAnimals.db").getPath(), null, SQLiteDatabase.OPEN_READONLY);

        // Columnas que deseo traer
        String[] projection = {"CodigoPedido", "IDCliente", "Cliente", "Sucursal", "Direccion", "Ciudad", "Telefono", "Items", "ValorFinal", "IVA", "Observaciones", "FechaCreacion", "FechaEntrega" };

        // busqueda a realizar
        String selection = "CodigoVendedor = ?";

        // Parametros para buscar
        String[] selectionArgs = {CodigoVendedor};

        // Ejecutar la consulta filtrando por el codigo del cliente
        cursor = db.query("PedidosLocal", projection, selection, selectionArgs, null, null, null);

        // Recorrer los resultados y agregar las filas a la tabla
        while (cursor.moveToNext()) {

            String CodigoPed = cursor.getString(0);
            String IDCliente = cursor.getString(1);
            String Cliente = cursor.getString(2);
            String Sucursal = cursor.getString(3);
            String Direccion = cursor.getString(4);
            String Ciudad = cursor.getString(5);
            String Telefono = cursor.getString(6);
            String Items = cursor.getString(7);
            String ValorFinal = cursor.getString(8);
            String IVA = cursor.getString(9);
            String Observaciones = cursor.getString(10);
            String FechaCreacion = cursor.getString(11);
            String FechaEntrega = cursor.getString(12);

            // Crear una nueva fila
            TableRow row = new TableRow(this);

            // Crear el valor de Referencia
            TextView Ref = new TextView(this);
            Ref.setText(" " + CodigoPed + " ");
            Ref.setTextSize(22);
            Ref.setTextColor(0xFFFF0000);
            Ref.setBackgroundResource(R.drawable.bordetablaceldas);
            Ref.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            Ref.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Saltar a la ventana de confirmacion
                    Intent intent = new Intent(ListaPedidos.this, ResumenPedido.class);
                    intent.putExtra("CodigoPedido", CodigoPed);
                    intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                    startActivity(intent);
                    finish();

                }
            });
            row.addView(Ref);

            // Crear el valor de Tipo
            TextView Tip = new TextView(this);
            Tip.setText(" " + IDCliente + " ");
            Tip.setTextSize(22);
            Tip.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Tip);

            // Crear el valor de Marca
            TextView Marc = new TextView(this);
            Marc.setText(" " + Cliente + " ");
            Marc.setTextSize(22);
            Marc.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Marc);

            // Crear el valor de Nombre
            TextView Nom = new TextView(this);
            Nom.setText(" " + Sucursal + " ");
            Nom.setTextSize(22);
            Nom.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Nom);

            // Crear el valor de Presentacion
            TextView Pres = new TextView(this);
            Pres.setText(" " + Direccion + " ");
            Pres.setTextSize(22);
            Pres.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Pres);

            // Crear el valor de Ciudad
            TextView Ciud = new TextView(this);
            Ciud.setText(" " + Ciudad + " ");
            Ciud.setTextSize(22);
            Ciud.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Ciud);

            // Crear el valor de Descripcion
            TextView Des = new TextView(this);
            Des.setText(" " + Telefono + " ");
            Des.setTextSize(22);
            Des.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Des);

            // Crear el valor de Cantidad
            TextView Ctd = new TextView(this);
            Ctd.setText(" " + Items + " ");
            Ctd.setTextSize(22);
            Ctd.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Ctd);

            // Crear el valor de Valor Unitario
            TextView ValUn = new TextView(this);
            ValUn.setText(" " + NumeroAjustado(ValorFinal) + " ");
            ValUn.setTextSize(22);
            ValUn.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(ValUn);

            // Crear el valor de Descuento
            TextView Desc = new TextView(this);
            Desc.setText(" " + NumeroAjustado(IVA) + " ");
            Desc.setTextSize(22);
            Desc.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Desc);

            // Crear el valor de CantidadAdicional
            TextView CtdAd = new TextView(this);
            CtdAd.setText(" " + Observaciones + " ");
            CtdAd.setTextSize(22);
            CtdAd.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(CtdAd);

            // Crear el valor de Valor Total
            TextView ValTot = new TextView(this);
            ValTot.setText(" " + FechaCreacion + " ");
            ValTot.setTextSize(22);
            ValTot.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(ValTot);

            // Crear el valor de Observaciones
            TextView Obs = new TextView(this);
            Obs.setText(" " + FechaEntrega + " ");
            Obs.setTextSize(22);
            Obs.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Obs);

            // Agregar toda la fila a la tabla
            mTabla.addView(row);

        }

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
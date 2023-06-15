package com.example.animalsdelllano;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EliminarPedidos extends AnularBotonRetroceso {

    private TextView NombreUsu;
    private TableLayout mTabla;
    private ImageButton retroceso;

    ArrayList<String> DatosUsuario;

    // Abrir la conexion con la base de datos y consultar los usuarios
    BasesDeDatos dbU = new BasesDeDatos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_pedidos);

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
                Intent intent = new Intent(EliminarPedidos.this, Vendedor.class);
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
        String[] projection = {"CodigoPedido", "IDCliente", "Cliente", "Sucursal", "Direccion", "Telefono", "Items", "ValorFinal", "IVA", "Observaciones", "FechaCreacion", "FechaEntrega" };

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
            String Items = cursor.getString(6);
            String ValorFinal = cursor.getString(7);
            String IVA = cursor.getString(8);
            String FechaCreacion = cursor.getString(10);

            // Crear una nueva fila
            TableRow row = new TableRow(this);

            // Crear el valor de Referencia
            TextView Ref = new TextView(this);
            Ref.setText(" " + CodigoPed + " ");
            Ref.setTextSize(22);
            Ref.setBackgroundResource(R.drawable.bordetablaceldas);
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

            // Crear el valor de Valor Total
            TextView ValTot = new TextView(this);
            ValTot.setText(" " + FechaCreacion + " ");
            ValTot.setTextSize(22);
            ValTot.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(ValTot);

            // Crear un boton para borrar el pedido
            TextView borraritem = new TextView(this);
            borraritem.setTag(row);
            borraritem.setGravity(Gravity.CENTER);
            borraritem.setBackgroundResource(R.drawable.bordetablaceldas);
            borraritem.setTextSize(22);
            borraritem.setText("X");
            borraritem.setTextColor(0xFFFF0000);
            borraritem.setTypeface(null, Typeface.BOLD);
            borraritem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EliminarPedidos.this);
                    builder.setMessage("¿Estás seguro que deseas eliminar el pedido? Esta acción es irreversible y deberás volver a crear el pedido")
                            .setTitle("Confirmación")
                            .setCancelable(false)
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id2) {

                                    // Acción a realizar si el usuario confirma
                                    // El usuario debe agregar su contraseña para confirmar la eliminacion del pedido
                                    dialog.cancel();
                                    TableRow row = (TableRow) v.getTag();
                                    ValidarContrasena(CodigoPed, row, mTabla);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id2) {
                                    // Acción a realizar si el usuario cancela
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            row.addView(borraritem);

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

    public void ValidarContrasena(String CodigoPed, TableRow row, TableLayout mTabla){

        // Crea un AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configura el título y el mensaje del AlertDialog
        builder.setTitle("Ingresar contraseña");
        builder.setMessage("Por favor, ingresa tu contraseña de la aplicación para confirmar la eliminación del pedido:");

        // Crea un EditText para el cuadro de texto
        final EditText contra = new EditText(this);
        builder.setView(contra);

        // Configura los botones del AlertDialog
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtiene el texto ingresado
                String contrasena = contra.getText().toString();

                // Valida que el texto no esté vacío
                if (contrasena.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "El texto no puede estar vacío", Toast.LENGTH_SHORT).show();

                } else {

                    // Validar que la constraseña sea la correcta
                    Boolean resultado = dbU.validarUsuarios(DatosUsuario.get(0),contrasena);

                    // Comprobar que los campos de usuario y contraseña esten almacenados en la base de datos
                    if (resultado == true) {

                        // Borrar el pedido
                        dbU.borrarPedido(CodigoPed);
                        mTabla.removeView(row);

                    } else {
                        dialog.cancel();
                        Toast.makeText(EliminarPedidos.this, "Contraseña incorrecta. Ingresala de nuevo.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        // Crea y muestra el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
package com.example.animalsdelllano;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResumenProductos extends AnularBotonRetroceso {

    // Codigo del pedido
    String CodigoPedido;
    int Items = 0;
    int Productos = 0;
    int SubTotal = 0;
    double IVA = 0;
    double Total = 0;

    // Crear las variables de las variables y los campos de texto
    private TextView NombreUsu;
    private TableLayout mTabla;
    private ImageButton retroceso;
    private Button CrearPedido;

    private EditText Obs;
    int CantidadProductos;

    String IDCliente;
    String Cliente;
    String Sucursal;
    String Direccion;
    String Ciudad;
    String Telefono;
    String Observaciones;

    ArrayList<String> ClienteC = new ArrayList<>();
    ArrayList<String> DatosUsuario = new ArrayList<>();
    ArrayList<String> ProductoAL = new ArrayList<>();
    ArrayList<String> DatosCliente = new ArrayList<>();
    BasesDeDatos dbPL = new BasesDeDatos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedidos);

        // Relacionarla con el objeto visual
        Obs = findViewById(R.id.Comentarios);
        mTabla = findViewById(R.id.Productos);
        retroceso = findViewById(R.id.retroceder);
        NombreUsu = findViewById(R.id.nombreUser);
        CrearPedido = findViewById(R.id.CrearPed);

        // Recibir el codigo del pedido
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
        Ciudad = DatosCliente.get(4);
        Telefono = DatosCliente.get(5);

        ClienteC.add(IDCliente);
        ClienteC.add(Cliente);
        ClienteC.add(Sucursal);
        ClienteC.add(Direccion);
        ClienteC.add(Ciudad);
        ClienteC.add(Telefono);

        // Funcionalidad del boton de retroceso
        retroceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResumenProductos.this, AgregarProductos.class);
                intent.putExtra("CodigoPedido", CodigoPedido);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                intent.putStringArrayListExtra("InformacionCliente", ClienteC);
                startActivity(intent);
                finish();
            }
        });

        CrearTabla(CodigoPedido);

        // Funcionalidad del boton crear pedido
        CrearPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validar que el pedido tenga al menos un producto
                ConsultarProductos(CodigoPedido);
                if (CantidadProductos == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ResumenProductos.this);
                    builder1.setMessage("No puedes crear el pedido si no has relacionado al menos 1 producto").setTitle("Información").setCancelable(false).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert1 = builder1.create();
                    alert1.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResumenProductos.this);
                    builder.setMessage("¿Estás seguro que deseas crear el pedido? Revisa detenidamente los productos, valores y cantidades finales.")
                            .setTitle("Confirmación")
                            .setCancelable(false)
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id2) {

                                    // Creacion del pedido
                                    Observaciones = Obs.getText().toString();

                                    // Obtener la fecha de hoy
                                    Calendar calendario = Calendar.getInstance();
                                    Date fechaact = calendario.getTime();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String fechaactual = dateFormat.format(fechaact);
                                    String fechahabil;

                                    // Dia habil siguiente
                                    fechahabil = DiaHabil(calendario);

                                    // Crear el pedido
                                    dbPL.AgregarPedidoLocal(CodigoPedido, IDCliente, Cliente, Sucursal, Direccion, Ciudad, Double.valueOf(Telefono), Items, Total, IVA, Observaciones, fechaactual, fechahabil, DatosUsuario.get(0), 0);
                                    Toast.makeText(ResumenProductos.this, "La fecha estimada de entrega del pedido es: " + fechahabil, Toast.LENGTH_LONG).show();

                                    // Saltar a la ventana de confirmacion
                                    Intent intent = new Intent(ResumenProductos.this, ConfirmacionPedido.class);
                                    intent.putExtra("CodigoPedido", CodigoPedido);
                                    intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                                    startActivity(intent);
                                    finish();

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
            }
        });

    }

    public void CrearTabla(String CodigoPedido){

        // Conectar a la base de datos
        SQLiteDatabase db = null;
        Cursor cursor = null;

        // Abrir la base de datos en modo lectura
        db = SQLiteDatabase.openDatabase(getDatabasePath("ProductosAnimals.db").getPath(), null, SQLiteDatabase.OPEN_READONLY);

        // Columnas que deseo traer
        String[] projection = {"CodigoPedido, Referencia", "Tipo", "Marca", "Nombre", "Presentacion", "Descripcion", "ValorUnitario", "Cantidad", "Descuento", "CantidadAdicional", "ValorTotal", "Observaciones" };

        // busqueda a realizar
        String selection = "CodigoPedido = ?";

        // Parametros para buscar
        String[] selectionArgs = {CodigoPedido};

        // Ejecutar la consulta filtrando por el codigo de pedido
        cursor = db.query("ProductosLocal", projection, selection, selectionArgs, null, null, null);
        CantidadProductos = cursor.getCount();

        // Recorrer los resultados y agregar las filas a la tabla
        while (cursor.moveToNext()) {

            String CodigoPed = cursor.getString(0);
            String Referencia = cursor.getString(1);
            String Tipo = cursor.getString(2);
            String Marca = cursor.getString(3);
            String Nombre = cursor.getString(4);
            String Presentacion = cursor.getString(5);
            String Descripcion = cursor.getString(6);
            String ValorUnitario = cursor.getString(7);
            String Cantidad = cursor.getString(8);
            String Descuento = cursor.getString(9);
            String CantidadAdicional = cursor.getString(10);
            String ValorTotal = cursor.getString(11);
            String Observaciones = cursor.getString(12);

            // Crear una nueva fila
            TableRow row = new TableRow(this);

            // Crear el valor de Referencia
            TextView Ref = new TextView(this);
            Ref.setText(" " + Referencia + " ");
            Ref.setTextSize(22);
            Ref.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Ref);

            // Crear el valor de Tipo
            TextView Tip = new TextView(this);
            Tip.setText(" " + Tipo + " ");
            Tip.setTextSize(22);
            Tip.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Tip);

            // Crear el valor de Marca
            TextView Marc = new TextView(this);
            Marc.setText(" " + Marca + " ");
            Marc.setTextSize(22);
            Marc.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Marc);

            // Crear el valor de Nombre
            TextView Nom = new TextView(this);
            Nom.setText(" " + Nombre + " ");
            Nom.setTextSize(22);
            Nom.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Nom);

            // Crear el valor de Presentacion
            TextView Pres = new TextView(this);
            Pres.setText(" " + Presentacion + " ");
            Pres.setTextSize(22);
            Pres.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Pres);

            // Crear el valor de Descripcion
            TextView Des = new TextView(this);
            Des.setText(" " + Descripcion + " ");
            Des.setTextSize(22);
            Des.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Des);

            // Crear el valor de Valor Unitario
            TextView ValUn = new TextView(this);
            ValUn.setText(" " + NumeroAjustado(ValorUnitario) + " ");
            ValUn.setTextSize(22);
            ValUn.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(ValUn);

            // Crear el valor de Cantidad
            TextView Ctd = new TextView(this);
            Ctd.setText(" " + Cantidad + " ");
            Ctd.setTextSize(22);
            Ctd.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Ctd);

            // Crear el valor de Descuento
            TextView Desc = new TextView(this);
            Desc.setText(" " + Descuento + " ");
            Desc.setTextSize(22);
            Desc.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Desc);

            // Crear el valor de CantidadAdicional
            TextView CtdAd = new TextView(this);
            CtdAd.setText(" " + CantidadAdicional + " ");
            CtdAd.setTextSize(22);
            CtdAd.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(CtdAd);

            // Crear el valor de Valor Total
            TextView ValTot = new TextView(this);
            ValTot.setText(" " + NumeroAjustado(ValorTotal) + " ");
            ValTot.setTextSize(22);
            ValTot.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(ValTot);

            // Crear el valor de Observaciones
            TextView Obs = new TextView(this);
            Obs.setText(" " + Observaciones + " ");
            Obs.setTextSize(22);
            Obs.setBackgroundResource(R.drawable.bordetablaceldas);
            row.addView(Obs);

            // Crear un boton para modificar un item del pedido
            TextView modificaritem = new TextView(this);
            modificaritem.setTag(row);
            modificaritem.setGravity(Gravity.CENTER);
            modificaritem.setBackgroundResource(R.drawable.modificar);
            modificaritem.setTextSize(22);
            modificaritem.setText(" ");
            modificaritem.setTextColor(0xFFFF0000);
            modificaritem.setTypeface(null, Typeface.BOLD);
            modificaritem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Agregar todos los detalles del producto
                    ProductoAL.add(Referencia);
                    ProductoAL.add(Tipo);
                    ProductoAL.add(Marca);
                    ProductoAL.add(Nombre);
                    ProductoAL.add(Presentacion);
                    ProductoAL.add(Descripcion);
                    ProductoAL.add(ValorUnitario);
                    ProductoAL.add(Cantidad);
                    ProductoAL.add(Descuento);
                    ProductoAL.add(CantidadAdicional);
                    ProductoAL.add(Observaciones);

                    // Lanzar la vista de modificar y enviar todos los datos que corresponden
                    Intent intent = new Intent(ResumenProductos.this, ModificarProductos.class);
                    intent.putExtra("CodigoPedido", CodigoPedido);
                    intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                    intent.putStringArrayListExtra("InformacionProducto", ProductoAL);
                    intent.putStringArrayListExtra("InformacionCliente", ClienteC);
                    startActivity(intent);
                    finish();

                }
            });
            row.addView(modificaritem);

            // Crear un boton para borrar un item del pedido
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResumenProductos.this);
                    builder.setMessage("¿Estás seguro que deseas eliminar este elemento?")
                            .setTitle("Confirmación")
                            .setCancelable(false)
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id2) {
                                    // Acción a realizar si el usuario confirma
                                    TableRow row = (TableRow) v.getTag();
                                    mTabla.removeView(row);
                                    dbPL.borrarProducto(CodigoPed, Referencia);
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

            // Sumar los items
            Items = Items + 1;

            // Sumar los productos
            Productos = Productos + Integer.valueOf(Cantidad) + Integer.valueOf(CantidadAdicional);

            // Sumar los totales
            SubTotal = SubTotal + Integer.valueOf(ValorTotal);

        }

        // Obtener el IVA
        IVA = SubTotal * 0.19;

        // Sumar los totales mas el IVA
        Total = SubTotal + IVA;

        // Agregar el subtotal, iva y el total

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
    public void ConsultarProductos(String CodigoPedido){

        // Conectar a la base de datos
        BasesDeDatos dbPL = new BasesDeDatos(this);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{

            // Abrir la base de datos en modo lectura
            db = SQLiteDatabase.openDatabase(getDatabasePath("ProductosAnimals.db").getPath(), null, SQLiteDatabase.OPEN_READONLY);

            // Columnas que deseo traer
            String[] projection = {"CodigoPedido, Referencia", "Tipo", "Marca", "Nombre", "Presentacion", "Descripcion", "ValorUnitario", "Cantidad", "Descuento", "CantidadAdicional", "ValorTotal", "Observaciones" };

            // busqueda a realizar
            String selection = "CodigoPedido = ?";

            // Parametros para buscar
            String[] selectionArgs = {CodigoPedido};

            // Ejecutar la consulta filtrando por el codigo de pedido
            cursor = db.query("ProductosLocal", projection, selection, selectionArgs, null, null, null);
            CantidadProductos = cursor.getCount();

        } catch (
                SQLiteException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la base de datos y el cursor
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
    public String DiaHabil(Calendar calendar){

        calendar.add(Calendar.DAY_OF_YEAR, 1); // sumar un día

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 2); // sumar dos días para llegar al lunes
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1); // sumar un día para llegar al lunes
        }

        List<String> feriados = new ArrayList<>();
        feriados.add("01/01");
        feriados.add("01/05");
        feriados.add("20/07");
        feriados.add("07/08");
        feriados.add("08/12");
        feriados.add("25/12");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        String fechaActual = dateFormat.format(calendar.getTime());
        if (feriados.contains(fechaActual)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1); // sumar un día para llegar al siguiente día hábil
        }

        // Siguiente dia habil
        Date siguienteDiaHabil = calendar.getTime();

        // Convertir a string
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        String fechahabil = dateFormat2.format(siguienteDiaHabil);
        return fechahabil;

    }

}
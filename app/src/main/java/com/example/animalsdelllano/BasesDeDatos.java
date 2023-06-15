package com.example.animalsdelllano;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasesDeDatos extends SQLiteOpenHelper {

    // Definir el nombre y la version de la base de datos
    private static final String DATABASE_NAME = "ProductosAnimals.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor de la base de datos
    public BasesDeDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Metodos obligatorios de creacion y actualizacion
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Base de datos que captura la informacion de Excel
        db.execSQL("CREATE TABLE ProductosCloud (Referencia NUMERIC PRIMARY KEY, Tipo TEXT, Marca TEXT, Nombre TEXT, Presentacion TEXT, Descripcion TEXT, ValorUnitario NUMERIC, Stock NUMERIC)");

        // Base de Datos de Usuarios
        db.execSQL("CREATE TABLE Usuarios (Usuario TEXT PRIMARY KEY, Contrasena TEXT, Nombres TEXT, Apellidos TEXT, Correo TEXT, Rol TEXT, Subrol TEXT)");

        // Base de Datos de Pedidos
        db.execSQL("CREATE TABLE Pedidos (CodigoPedido TEXT PRIMARY KEY, IDCliente TEXT, Fecha DATETIME, Cliente TEXT, Sucursal TEXT, Direccion TEXT, Telefono TEXT, Items INTEGER, ValorFinal NUMERIC, IVA NUMERIC, Observaciones TEXT, FechaCreacion DATE, FechaEntrega DATE, CodigoVendedor TEXT)");

        // Base de Datos de Productos
        db.execSQL("CREATE TABLE Productos (Id INTEGER PRIMARY KEY AUTOINCREMENT, CodigoPedido TEXT, Referencia TEXT, Tipo TEXT, Marca TEXT, Nombre TEXT, Presentacion TEXT, Descripcion TEXT, ValorUnitario NUMERIC, Cantidad INTEGER, ValorTotal NUMERIC, Obervaciones TEXT, Descuento INTEGER, Estatus NUMERIC)");

        // Base de datos de Clientes
        db.execSQL("CREATE TABLE Clientes (Id INTEGER PRIMARY KEY AUTOINCREMENT, IDCliente TEXT, Cliente TEXT, Sucursal TEXT, Direccion TEXT, Ciudad TEXT, Telefono NUMERIC)");

        // Base de datos de Pedidos Local - Solo se usara para almacenar la estructura del pedido y cargarla a la base de datos en la nube
        db.execSQL("CREATE TABLE PedidosLocal (CodigoPedido TEXT PRIMARY KEY, IDCliente TEXT, Cliente TEXT, Sucursal TEXT, Direccion TEXT, Ciudad TEXT, Telefono NUMERIC, Items NUMERIC, ValorFinal NUMERIC, IVA NUMERIC, Observaciones TEXT, FechaCreacion TEXT, FechaEntrega TEXT, CodigoVendedor TEXT, Estatus NUMERIC)");

        // Base de datos de Productos Local - Solo se usara para almacenar la estructura del producto y cargarla a la base de datos en la nube
        db.execSQL("CREATE TABLE ProductosLocal (Id INTEGER PRIMARY KEY AUTOINCREMENT, CodigoPedido TEXT, Referencia TEXT, Tipo TEXT, Marca TEXT, Nombre TEXT, Presentacion TEXT, Descripcion TEXT, ValorUnitario NUMERIC, Cantidad INTEGER, Descuento INTEGER, CantidadAdicional NUMERIC, ValorTotal NUMERIC, Observaciones TEXT, CodigoVendedor TEXT, Estatus NUMERIC)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la versión de la base de datos ha cambiado, actualizar la estructura de la base de datos
        if (oldVersion < newVersion) {
            // Eliminar las tablas existentes, si es necesario
            db.execSQL("DROP TABLE IF EXISTS ProductosCloud");
            db.execSQL("DROP TABLE IF EXISTS Usuarios");
            db.execSQL("DROP TABLE IF EXISTS Pedidos");
            db.execSQL("DROP TABLE IF EXISTS Productos");
            db.execSQL("DROP TABLE IF EXISTS Clientes");
            db.execSQL("DROP TABLE IF EXISTS PedidosLocal");
            db.execSQL("DROP TABLE IF EXISTS ProductosLocal");

            // Crear las nuevas tablas
            onCreate(db);
        }
    }

    ///////////////////////// TABLA USUARIOS \\\\\\\\\\\\\\\\\\\\\\\\\
    // Crear los usuarios, contraseñas y perfiles de los usuarios de la aplicacion
    public void CrearUsuarios() {

        // Borrar tode el contenido de la base de datos
        borrarContenidoTabla("Usuarios");

        // Conectarse a la base de datos
        SQLiteDatabase db = getWritableDatabase();

        // Diccionario para agregar valores a una base de datos
        ContentValues valores = new ContentValues();

        // Ingresar todos los valores de la base de datos
        valores.put("Usuario", "JuanVendedor");
        valores.put("Contrasena", "Vendor1234");
        valores.put("Nombres", "Juan David");
        valores.put("Apellidos", "Gomez Suarez");
        valores.put("Correo", "juanda@hotmail.com");
        valores.put("Rol", "Vendedor");
        valores.put("Subrol", "Medicina");

        // Agregar los registros a la base de datos
        db.insert("Usuarios", null, valores);

        // Limpiar el diccionario para ingresar nuevos registros
        valores.clear();

        // Ingresar todos los valores de la base de datos
        valores.put("Usuario", "PabloVendedor");
        valores.put("Contrasena", "Vendor12345");
        valores.put("Nombres", "Pablo Esteban");
        valores.put("Apellidos", "Perez");
        valores.put("Correo", "pablope@hotmail.com");
        valores.put("Rol", "Vendedor");
        valores.put("Subrol", "Alimentos");

        // Agregar los registros a la base de datos
        db.insert("Usuarios", null, valores);

        // Ingresar todos los valores de la base de datos
        valores.put("Usuario", "Admin");
        valores.put("Contrasena", "Admin1234");
        valores.put("Nombres", "Juan Andres");
        valores.put("Apellidos", "Baldion Gongora");
        valores.put("Correo", "juan-andres-baldion@hotmail.com");
        valores.put("Rol", "Administrador");
        valores.put("Subrol", "All Access");

        // Agregar los registros a la base de datos
        db.insert("Usuarios", null, valores);

        // Ingresar todos los valores de la base de datos
        valores.put("Usuario", "Admin2");
        valores.put("Contrasena", "Admin12345");
        valores.put("Nombres", "Juan Sebastian");
        valores.put("Apellidos", "Calvo Cadena");
        valores.put("Correo", "juan.calvo@hotmail.com");
        valores.put("Rol", "Administrador");
        valores.put("Subrol", "All Access");

        // Agregar los registros a la base de datos
        db.insert("Usuarios", null, valores);

        // Ingresar todos los valores de la base de datos
        valores.put("Usuario", "j");
        valores.put("Contrasena", "1");
        valores.put("Nombres", "User");
        valores.put("Apellidos", "Test");
        valores.put("Correo", "test@hotmail.com");
        valores.put("Rol", "Vendedor");
        valores.put("Subrol", "Alimentos");

        // Agregar los registros a la base de datos
        db.insert("Usuarios", null, valores);

        // Cerrar la conexion a la base de datos
        db.close();
    }

    // Validar los usuarios
    public boolean validarUsuarios(String Usuario, String Password) {

        CrearUsuarios();

        // Conectarse a la base de datos
        SQLiteDatabase db = getReadableDatabase();

        // Consulta a la base de datos
        Cursor cursor = db.rawQuery("SELECT * FROM Usuarios WHERE Usuario=? AND Contrasena=?", new String[]{Usuario, Password});

        // Si existe el usuario la consulta tiene mas de un registro almacenado
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    // Obtener la informacion completa del usuario
    public ArrayList<String> informacionUsuarios (String Usuario, String Password){

        // Conectarse a la base de datos
        SQLiteDatabase db = getReadableDatabase();

        // Consulta a la base de datos
        Cursor cursor = db.rawQuery("SELECT * FROM Usuarios WHERE Usuario=? AND Contrasena=?", new String[]{Usuario, Password});

        // Crear el arreglo que contendra toda la informacion del usuario
        ArrayList<String> infoUsuario = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                infoUsuario.add(cursor.getString(cursor.getColumnIndex(columnName)));
            }
        }
        cursor.close();
        db.close();

        return infoUsuario;
    }

    ///////////////////////////// TABLA PRODUCTOS CLOUD \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void AgregarProductosExcel(ArrayList<ArrayList<String>> BaseDatosProductos) {

        // Borrar tode el contenido de la base de datos
        borrarContenidoTabla("ProductosCloud");

        // Conectarse a la base de datos
        SQLiteDatabase dbPC = getWritableDatabase();

        // Pasar la BaseDeDatos del Array a la base de datos definitiva SQLite
        ContentValues values = new ContentValues();

        // Definir cada columna que se ingresará a la base de datos
        Double Referencia;
        String Tipo;
        String Marca;
        String Nombre;
        String Descripcion;
        String Presentacion;
        Double ValorUnitario;
        Integer Stock;

        // Leer los valores del arraylist padre
        for (ArrayList<String> fila : BaseDatosProductos) {

            // Obtener cada valor del array y referenciarlo en una de las variables
            Referencia = Double.valueOf(fila.get(0));
            Tipo = fila.get(1).toString();
            Marca = fila.get(2).toString();
            Nombre = fila.get(3).toString();
            Descripcion = fila.get(4).toString();
            Presentacion = fila.get(5).toString();
            ValorUnitario = Double.valueOf(fila.get(6));
            Stock = Double.valueOf(fila.get(7)).intValue();

            // Agregarlo al formato apto por la base de datos
            values.put("Referencia", Referencia);
            values.put("Tipo", Tipo);
            values.put("Marca", Marca);
            values.put("Nombre", Nombre);
            values.put("Descripcion", Descripcion);
            values.put("Presentacion", Presentacion);
            values.put("ValorUnitario", ValorUnitario);
            values.put("Stock", Stock);

            // Insertar la fila en la base de datos
            dbPC.insert("ProductosCloud", null, values);
            values.clear();
        }
        // Cerrar la conexión con la base de datos
        dbPC.close();
    }

    // Consultar productos de la base de datos local
    public List<String> ObtenerValoresUnicos(String NombreColumna){

        SQLiteDatabase db = getReadableDatabase();
        List<String> nombresUnicos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + NombreColumna + " FROM ProductosCloud", null);
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndex(NombreColumna));
                nombresUnicos.add(nombre);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return nombresUnicos;

    }

    // Consultar marcas de la base de datos local
    public List<String> MarcasUnicas(CharSequence s){

        // Conexion a la base de datos y array para recibir los valores
        SQLiteDatabase db = getReadableDatabase();
        List<String> marcasUnicas = new ArrayList<>();

        // Consulta a la base de datos
        String query = "SELECT DISTINCT Marca FROM ProductosCloud WHERE Tipo = ?";
        String[] selectionArgs = { s.toString() };
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                marcasUnicas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return marcasUnicas;

    }

    // Consultar marcas de la base de datos local
    public List<String> NombresUnicos(CharSequence s1, CharSequence s2){

        // Conexion a la base de datos y array para recibir los valores
        SQLiteDatabase db = getReadableDatabase();
        List<String> marcasUnicas = new ArrayList<>();

        // Consulta a la base de datos
        String query = "SELECT DISTINCT Nombre FROM ProductosCloud WHERE Tipo = ? AND Marca = ?";
        String[] selectionArgs = { s1.toString(), s2.toString()};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                marcasUnicas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return marcasUnicas;

    }

    // Consultar presentaciones de la base de datos local
    public List<String> PresentacionesUnicas(CharSequence s1, CharSequence s2, CharSequence s3){

        // Conexion a la base de datos y array para recibir los valores
        SQLiteDatabase db = getReadableDatabase();
        List<String> presentacionesUnicas = new ArrayList<>();

        // Consulta a la base de datos
        String query = "SELECT DISTINCT Presentacion FROM ProductosCloud WHERE Tipo = ? AND Marca = ? AND Nombre = ?";
        String[] selectionArgs = { s1.toString(), s2.toString(), s3.toString()};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                presentacionesUnicas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return presentacionesUnicas;

    }

    // Obtener todos los valores de un registro en particular
    public ArrayList<HashMap<String, String>> ProductoCompleto(CharSequence s1, CharSequence s2, CharSequence s3, CharSequence s4){

        // Conexion a la base de datos y array para recibir los valores
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String, String>> ProductoUnico = new ArrayList<HashMap<String, String>>();

        // Consulta a la base de datos
        String query = "SELECT * FROM ProductosCloud WHERE Tipo = ? AND Marca = ? AND Nombre = ? AND Presentacion = ?";
        String[] selectionArgs = { s1.toString(), s2.toString(), s3.toString(), s4.toString()};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> fila = new HashMap<String, String>();
                fila.put("Referencia", cursor.getString(cursor.getColumnIndex("Referencia")));
                fila.put("Descripcion", cursor.getString(cursor.getColumnIndex("Descripcion")));
                fila.put("ValorUnitario", cursor.getString(cursor.getColumnIndex("ValorUnitario")));
                fila.put("Stock", cursor.getString(cursor.getColumnIndex("Stock")));
                ProductoUnico.add(fila);
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return ProductoUnico;

    }

    ///////////////////////////// BASE DE DATOS PRODUCTOS LOCAL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void AgregarProductoLocal(String CodigoPedido, String Referencia, String Tipo, String Marca, String Nombre, String Presentacion, String Descripcion, Integer ValorUnitario, Integer Cantidad, Integer Descuento, Integer CantidadAdicional, Integer ValorTotal, String Observaciones, String CodigoVendedor, Integer Estatus){

        // Conexion con la base de datos
        SQLiteDatabase db = getWritableDatabase();

        // Estructura para crear la fila
        ContentValues valores = new ContentValues();

        // Agregar cada valor de cada columna de forma individual
        valores.put("CodigoPedido", CodigoPedido);
        valores.put("Referencia", Referencia);
        valores.put("Tipo", Tipo);
        valores.put("Marca", Marca);
        valores.put("Nombre", Nombre);
        valores.put("Presentacion", Presentacion);
        valores.put("Descripcion", Descripcion);
        valores.put("ValorUnitario", ValorUnitario);
        valores.put("Cantidad", Cantidad);
        valores.put("Descuento", Descuento);
        valores.put("CantidadAdicional", CantidadAdicional);
        valores.put("ValorTotal", ValorTotal);
        valores.put("Observaciones", Observaciones);
        valores.put("CodigoVendedor", CodigoVendedor);
        valores.put("Estatus", Estatus);

        // Agregar los valores a la base de datos
        db.insert("ProductosLocal", null, valores);

        // Cerrar la conexion a la base de datos
        db.close();

    }

    // Modificar un producto de un pedido
    public void ModificarProductoLocal(String CodigoPedido, String Referencia, String Tipo, String Marca, String Nombre, String Presentacion, String Descripcion, Integer ValorUnitario, Integer Cantidad, Integer Descuento, Integer CantidadAdicional, Integer ValorTotal, String Observaciones, String CodigoVendedor, Integer Estatus){

        // Conexion con la base de datos
        SQLiteDatabase db = getWritableDatabase();

        // Estructura para crear la fila
        ContentValues valores = new ContentValues();

        // Agregar cada valor de cada columna de forma individual
        valores.put("CodigoPedido", CodigoPedido);
        valores.put("Referencia", Referencia);
        valores.put("Tipo", Tipo);
        valores.put("Marca", Marca);
        valores.put("Nombre", Nombre);
        valores.put("Presentacion", Presentacion);
        valores.put("Descripcion", Descripcion);
        valores.put("ValorUnitario", ValorUnitario);
        valores.put("Cantidad", Cantidad);
        valores.put("Descuento", Descuento);
        valores.put("CantidadAdicional", CantidadAdicional);
        valores.put("ValorTotal", ValorTotal);
        valores.put("Observaciones", Observaciones);
        valores.put("CodigoVendedor", CodigoVendedor);
        valores.put("Estatus", Estatus);

        // Agregar los valores a la base de datos
        db.update("ProductosLocal",valores,"CodigoPedido = ? AND Referencia = ? AND Tipo = ? AND Marca = ? AND Nombre = ? AND Presentacion = ?", new String[] { CodigoPedido, Referencia, Tipo, Marca, Nombre, Presentacion});

        // Cerrar la conexion a la base de datos
        db.close();

    }

    // Borrar un producto de un pedido
    public void borrarProducto(String CodigoPedido, String Referencia){

        SQLiteDatabase db = getReadableDatabase();
        db.delete("ProductosLocal", "CodigoPedido = ? AND Referencia = ?", new String[] { CodigoPedido, Referencia});
        db.close();

    }

    ///////////////////////////// BASE DE DATOS PEDIDOS LOCAL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void AgregarPedidoLocal(String CodigoPedido, String IDCliente, String Cliente, String Sucursal, String Direccion, String Ciudad, Double Telefono, Integer Items, Double ValorFinal, Double IVA, String Observaciones, String FechaCreacion, String FechaEntrega, String CodigoVendedor, Integer Estatus){

        // Conexion con la base de datos
        SQLiteDatabase db = getWritableDatabase();

        // Estructura para crear la fila
        ContentValues valores = new ContentValues();

        // Agregar cada valor de cada columna de forma individual
        valores.put("CodigoPedido", CodigoPedido);
        valores.put("IDCliente", IDCliente);
        valores.put("Cliente", Cliente);
        valores.put("Sucursal", Sucursal);
        valores.put("Direccion", Direccion);
        valores.put("Ciudad", Ciudad);
        valores.put("Telefono", Telefono);
        valores.put("Items", Items);
        valores.put("ValorFinal", ValorFinal);
        valores.put("IVA", IVA);
        valores.put("Observaciones", Observaciones);
        valores.put("FechaCreacion", FechaCreacion);
        valores.put("FechaEntrega", FechaEntrega);
        valores.put("CodigoVendedor", CodigoVendedor);
        valores.put("Estatus", Estatus);

        // Agregar los valores a la base de datos
        db.insert("PedidosLocal", null, valores);

        // Cerrar la conexion a la base de datos
        db.close();

    }

    public void borrarPedido(String CodigoPedido){

        SQLiteDatabase db = getReadableDatabase();
        db.delete("PedidosLocal", "CodigoPedido = ?", new String[] { CodigoPedido});
        db.close();

    }

    // Obtener la informacion completa del usuario
    public ArrayList<String> PedidosCreados (String CodigoVendedor){

        // Conectarse a la base de datos
        SQLiteDatabase db = getReadableDatabase();

        // Consulta a la base de datos
        Cursor cursor = db.rawQuery("SELECT * FROM PedidosLocal WHERE CodigoVendedor = ?", new String[]{CodigoVendedor});

        // Crear el arreglo que contendra toda la informacion del usuario
        ArrayList<String> pedidos = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                pedidos.add(cursor.getString(cursor.getColumnIndex(columnName)));
            }
        }
        cursor.close();
        db.close();

        return pedidos;
    }

    ///////////////////////////// BASE DE DATOS CLIENTES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public List<String> nombresClientes(String NombreColumna){

        SQLiteDatabase db = getReadableDatabase();
        List<String> nombresClientes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + NombreColumna + " FROM Clientes", null);
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndex(NombreColumna));
                nombresClientes.add(nombre);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return nombresClientes;

    }

    // Consultar sucursales de la base de datos local
    public List<String> SucursalesUnicas(CharSequence s){

        // Conexion a la base de datos y array para recibir los valores
        SQLiteDatabase db = getReadableDatabase();
        List<String> sucursalesUnicas = new ArrayList<>();

        // Consulta a la base de datos
        String query = "SELECT DISTINCT Sucursal FROM Clientes WHERE Cliente = ?";
        String[] selectionArgs = { s.toString() };
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                sucursalesUnicas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return sucursalesUnicas;

    }

    public void AgregarClientes(ArrayList<ArrayList<String>> BaseClientes) {

        // Borrar tode el contenido de la base de datos
        borrarContenidoTabla("Clientes");

        // Conectarse a la base de datos
        SQLiteDatabase dbCl = getWritableDatabase();

        // Pasar la BaseDeDatos del Array a la base de datos definitiva SQLite
        ContentValues values = new ContentValues();

        // Definir cada columna que se ingresará a la base de datos
        String NIT;
        String Cliente;
        String Sucursal;
        String Direccion;
        String Ciudad;
        Double Numero;

        // Leer los valores del arraylist padre
        for (ArrayList<String> fila : BaseClientes) {

            // Obtener cada valor del array y referenciarlo en una de las variables
            NIT = fila.get(0);
            Cliente = fila.get(1);
            Sucursal = fila.get(2);
            Direccion = fila.get(3);
            Ciudad = fila.get(4);
            Numero = Double.valueOf(fila.get(5));

            // Agregarlo al formato apto por la base de datos
            values.put("IDCliente", NIT);
            values.put("Cliente", Cliente);
            values.put("Sucursal", Sucursal);
            values.put("Direccion", Direccion);
            values.put("Ciudad", Ciudad);
            values.put("Telefono", Numero);

            // Insertar la fila en la base de datos
            dbCl.insert("Clientes", null, values);
            values.clear();
        }
        // Cerrar la conexión con la base de datos
        dbCl.close();
    }

    // Obtener todos los valores de un registro en particular
    public ArrayList<HashMap<String, String>> ClienteCompleto(CharSequence s1, CharSequence s2){

        // Conexion a la base de datos y array para recibir los valores
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String, String>> ClienteUnico = new ArrayList<HashMap<String, String>>();

        // Consulta a la base de datos
        String query = "SELECT * FROM Clientes WHERE Cliente = ? AND Sucursal = ?";
        String[] selectionArgs = { s1.toString(), s2.toString()};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> fila = new HashMap<String, String>();
                fila.put("IDCliente", cursor.getString(cursor.getColumnIndex("IDCliente")));
                fila.put("Direccion", cursor.getString(cursor.getColumnIndex("Direccion")));
                fila.put("Ciudad", cursor.getString(cursor.getColumnIndex("Ciudad")));
                fila.put("Telefono", cursor.getString(cursor.getColumnIndex("Telefono")));
                ClienteUnico.add(fila);
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return ClienteUnico;

    }

    ///////////////////////////// FUNCION GENERAL PARA BORRAR DATOS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void borrarContenidoTabla(String nombreTabla){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(nombreTabla, null, null);
        db.close();
    }

}

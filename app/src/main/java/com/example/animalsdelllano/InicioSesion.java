package com.example.animalsdelllano;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class InicioSesion extends AnularBotonRetroceso {

    // Definimos como variables los campos de texto y los botones
    private EditText Usuario;
    private EditText Contrasena;
    private Button Acceder;

    // URL en el que se encuentra el archivo Excel base a ser descargado
    String URL_EXCEL = "https://drive.google.com/uc?export=download&id=1Ro0iPNTMchWm5ORsCwNKCMSSQD4CpeKX";
    String URL_CLIENTES = "https://drive.google.com/uc?export=download&id=1RoVm3ba5COrvO-S3khf4XStdWximVJ-E";

    // Arreglo para capturar todos los datos del usuario
    private ArrayList<String> infoUsuario = new ArrayList<>();

    // Crear la base de datos en forma de arraylist de arraylist. Un arraylist padre que almacena cada fila y otro que almacena las columnas
    ArrayList<ArrayList<String>> BaseDatosProductos = new ArrayList<>();
    ArrayList<ArrayList<String>> BaseClientes = new ArrayList<>();

    // Variable para determinar el estado de conexion a internet
    private Boolean ConexionAInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inicio_sesion);
        //this.deleteDatabase("ProductosAnimals.db");

        // Relacionar los campos con los objetos del layout
        Usuario = findViewById(R.id.usuario);
        Contrasena = findViewById(R.id.contrasena);
        Acceder = findViewById(R.id.iniciarsesion);

        // Abrir la conexion con la base de datos y consultar los usuarios
        BasesDeDatos dbU = new BasesDeDatos(this);

        // Abrir la conexion con la base de datos de excel y descargar los datos
        // Si el usuario tiene internet hacer la descarga, si no utilizar la ultima copia de la base de datos
        ConexionAInternet = ConexionInternet();

        // Validar la conexión y hacer la descarga
        if (ConexionAInternet){ // Si hay internet
            new LeerExcel().execute();
            // Limpiamos la base de datos de los pedidos locales

        } else { // Si no hay internet
            Toast.makeText(InicioSesion.this, "No tienes conexión a internet. Ten en cuenta que los productos y cantidades disponibles pueden estar desactualizados hasta que tengas una conexión estable a internet", Toast.LENGTH_LONG).show();
            // No limpiamos la base de datos

        }

        Acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Capturar los valores de los campos de texto
                String Usu = Usuario.getText().toString();
                String Pass = Contrasena.getText().toString();

                // Obtener la informacion del Usuario
                infoUsuario = dbU.informacionUsuarios(Usu, Pass);

                // utilizando el método join() de la clase TextUtils
                // String nombresConcatenados = TextUtils.join(", ", infoUsuario);

                // Evaluar si el usuario existe o no
                Boolean resultado = dbU.validarUsuarios(Usu,Pass);

                // Comprobar que los campos de usuario y contraseña esten almacenados en la base de datos
                if (resultado == true) {

                    // Traer la informacion del usuario
                    infoUsuario = dbU.informacionUsuarios(Usu, Pass);
                    Intent intent = new Intent(InicioSesion.this, Vendedor.class);
                    intent.putStringArrayListExtra("InformacionUsuario", infoUsuario);
                    startActivity(intent);
                    finish();
                    Toast.makeText(InicioSesion.this, "Bienvenido a VetAssist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InicioSesion.this, "Datos incorrectos. Comprueba que tode este bien escrito e intenta de nuevo.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Metodo para validar si el usuario esta conectado a internet o no
    private boolean ConexionInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Metodo para descargar la informacion del excel de productos
    private class LeerExcel extends AsyncTask<Void, Void, Void> {

        // Crear el objeto de la hoja de calculo
        private Workbook workbook;
        private Workbook workbookClientes;

        // Hacer la conexion con la base de datos en excel
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                // Leer la URL y descargar los archivos
                URL url = new URL(URL_EXCEL);
                URL urlCl = new URL(URL_CLIENTES);

                // Leer el archivo descargado
                InputStream inputStream = url.openStream();
                InputStream inputStream2 = urlCl.openStream();

                // Crear un objeto de tipo Microsoft Excel
                workbook = new XSSFWorkbook(inputStream);
                workbookClientes = new XSSFWorkbook(inputStream2);

                inputStream.close();
                inputStream2.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Hacer la lectura de los datos de la base de datos
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Validar que el libro no este vacio
            if (workbook != null && workbookClientes != null) {

                //////////////////////////////////////// PRODUCTOS
                // Determinar la hoja de la que se extraeran los datos
                Sheet sheet = workbook.getSheetAt(0);

                // Inicializar variable del numero de filas que se van a utilizar.
                int numRows = 0;
                boolean continuar = true;

                // Ciclo para contar la cantidad de productos. Cada producto es una fila de la hoja
                while (continuar) {

                    // Comprobar si la fila tiene datos o no
                    Row row = sheet.getRow(numRows);
                    Cell cell = row.getCell(0);
                    String valorF;

                    if (cell == null) {
                        valorF = "";
                    } else if (cell.getCellType() == CellType.STRING) {
                        valorF = cell.getStringCellValue();
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        valorF = String.valueOf(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.BOOLEAN) {
                        valorF = String.valueOf(cell.getBooleanCellValue());
                    } else {
                        valorF = "";
                    }
                    if (valorF.equals("")){
                        continuar = false;
                    } else {
                        numRows++;
                    }
                }

            /* Una vez determinado el numero de filas. Se hace un ciclo for para recorrer cada una de las filas y columnas.
            Cada fila se almacena en un array list que al final de barrer todas las columnas se guarda en el arraylist padre.
            */
                // Barrer las filas. Desde la 1 porque la 0 son los encabezados.
                for (int i = 1 ; i < numRows; i++) {

                    // Barrer las columnas. ESTO SE ADAPTA SEGUN LA CANTIDAD DE COLUMNAS FINALES QUE TENGA LA BASE DE DATOS. EN EXCEL.
                    // Crear matriz que almacena una fila completa de la base de datos
                    ArrayList<String> Producto = new ArrayList<>();

                    // Ciclo for para barrer las columnas. Indicar el numero de la columna Inicial y de la columna final.
                    for (int j = 0 ; j <= 7; j++) {

                        // Para obtener un valor se debe apuntar a la celda exacta
                        Row fila = sheet.getRow(i);
                        Cell celda = fila.getCell(j);

                        // Obtener el tipo de dato y contenido de la celda
                        if (celda == null) {
                            Producto.add("");
                        } else if (celda.getCellType() == CellType.STRING) {
                            Producto.add(celda.getStringCellValue());
                        } else if (celda.getCellType() == CellType.NUMERIC) {
                            Producto.add(String.valueOf(celda.getNumericCellValue()));
                        } else if (celda.getCellType() == CellType.BOOLEAN) {
                            Producto.add(String.valueOf(celda.getBooleanCellValue()));
                        } else {
                            Producto.add("");
                        }

                        // Si es la ultima, agregar tode el array a la base de datos
                        if (j == 7){
                            BaseDatosProductos.add(Producto);
                        }
                    }
                }

                // Pasar la BaseDeDatos del Array a la base de datos definitiva SQLite
                BasesDeDatos dbPC = new BasesDeDatos(InicioSesion.this);
                dbPC.AgregarProductosExcel(BaseDatosProductos);

                ///////////////////////////////////////// CLIENTES
                // Determinar la hoja de la que se extraeran los datos

                Sheet sheetCL = workbookClientes.getSheetAt(0);

                // Inicializar variable del numero de filas que se van a utilizar.
                numRows = 0;
                continuar = true;

                // Ciclo para contar la cantidad de productos. Cada producto es una fila de la hoja
                while (continuar) {

                    // Comprobar si la fila tiene datos o no
                    Row row = sheetCL.getRow(numRows);
                    Cell cell = row.getCell(1);
                    String valorF;

                    if (cell == null) {
                        valorF = "";
                    } else if (cell.getCellType() == CellType.STRING) {
                        valorF = cell.getStringCellValue();
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        valorF = String.valueOf(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.BOOLEAN) {
                        valorF = String.valueOf(cell.getBooleanCellValue());
                    } else {
                        valorF = "";
                    }
                    if (valorF.equals("")){
                        continuar = false;
                    } else {
                        numRows++;
                    }
                }

            /* Una vez determinado el numero de filas. Se hace un ciclo for para recorrer cada una de las filas y columnas.
            Cada fila se almacena en un array list que al final de barrer todas las columnas se guarda en el arraylist padre. */

                // Barrer las filas. Desde la 1 porque la 0 son los encabezados.
                for (int i = 1 ; i < numRows; i++) {

                    // Barrer las columnas. ESTO SE ADAPTA SEGUN LA CANTIDAD DE COLUMNAS FINALES QUE TENGA LA BASE DE DATOS. EN EXCEL.
                    // Crear matriz que almacena una fila completa de la base de datos
                    ArrayList<String> Cliente = new ArrayList<>();

                    // Ciclo for para barrer las columnas. Indicar el numero de la columna Inicial y de la columna final.
                    for (int j = 0 ; j <= 5; j++) {

                        // Para obtener un valor se debe apuntar a la celda exacta
                        Row fila = sheetCL.getRow(i);
                        Cell celda = fila.getCell(j);

                        // Obtener el tipo de dato y contenido de la celda
                        if (celda == null) {
                            Cliente.add("");
                        } else if (celda.getCellType() == CellType.STRING) {
                            Cliente.add(celda.getStringCellValue());
                        } else if (celda.getCellType() == CellType.NUMERIC) {
                            Cliente.add(String.valueOf(celda.getNumericCellValue()));
                        } else if (celda.getCellType() == CellType.BOOLEAN) {
                            Cliente.add(String.valueOf(celda.getBooleanCellValue()));
                        } else {
                            Cliente.add("");
                        }

                        // Si es la ultima, agregar tode el array a la base de datos
                        if (j == 5){
                            BaseClientes.add(Cliente);
                        }
                    }
                }

                // Pasar la BaseDeDatos del Array a la base de datos definitiva SQLite
                BasesDeDatos dbCl = new BasesDeDatos(InicioSesion.this);
                dbCl.AgregarClientes(BaseClientes);

                // cerrar el libro de Excel
                try {
                    workbook.close();
                   // workbookClientes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
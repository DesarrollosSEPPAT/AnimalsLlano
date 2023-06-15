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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AgregarProductos extends AnularBotonRetroceso {

    // Codigo del pedido
    String CodigoPedido;
    private ImageButton retroceso;

    // Gestor de ejecucion cada cierto tiempo
    private Handler handler = new Handler();
    private Runnable runnable;

    // Array que recibe las posibles opciones del Tipo de producto
    List<String> TipoProductos = new ArrayList<String>();
    List<String> MarcaProductos = new ArrayList<String>();
    List<String> NombreProductos = new ArrayList<String>();
    List<String> PresentacionProductos = new ArrayList<String>();

    ArrayList<String> ClienteC = new ArrayList<>();

    ArrayList<HashMap<String, String>> ProductoTotal = new ArrayList<>();

    // Crear las variables de las variables y los campos de texto
    private TextView NombreUsu;
    private String VUn;

    String IDCliente;
    String Cliente;
    String Sucursal;
    String Direccion;
    String Ciudad;
    String Telefono;

    // Crear la instancia de la clase Bases de Datos
    BasesDeDatos dbPC = new BasesDeDatos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_productos);

        NombreUsu = findViewById(R.id.nombreUser);

        // Recibir el codigo del pedido
        Intent intent = getIntent();
        CodigoPedido = intent.getStringExtra("CodigoPedido");
        ArrayList<String> DatosUsuario = intent.getStringArrayListExtra("InformacionUsuario");
        String NombreCompleto = "Hola " + DatosUsuario.get(2) + " " + DatosUsuario.get(3);
        NombreUsu.setText(NombreCompleto);

        // Recibir los datos del cliente
        ArrayList<String> DatosCliente = intent.getStringArrayListExtra("InformacionCliente");
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

        // Obtener los valores unicos del tipo de producto
        TipoProductos = dbPC.ObtenerValoresUnicos("Tipo");

        // Colocar las opciones del Tipo de producto
        AutoCompleteTextView TipoAC = findViewById(R.id.tipoProducto);
        AutoCompleteTextView MarcaAC = findViewById(R.id.Marca);
        AutoCompleteTextView NombreAC = findViewById(R.id.Nombre);
        AutoCompleteTextView PresentacionAC = findViewById(R.id.Presentacion);

        TextView ReferenciaTW = findViewById(R.id.Referencia);
        TextView DescripcionTW = findViewById(R.id.Descripcion);
        TextView ValorUnitarioTW = findViewById(R.id.valorUnitario);

        EditText CantidadET = findViewById(R.id.Cantidades);
        EditText DescuentoET = findViewById(R.id.descuento);
        EditText CantidadDescuentoET = findViewById(R.id.CantidadesDescuento);
        EditText ComentariosET = findViewById(R.id.Comentarios);

        // De inicio esta deshabilitado los botones. Solo se habilitan cuando el contenido del texto anterior sea valido
        MarcaAC.setEnabled(false);
        NombreAC.setEnabled(false);
        PresentacionAC.setEnabled(false);

        // Configurar parametros visuales
        Toast.makeText(getApplicationContext(), "Para ingresar un producto, diligencia los campos en el orden presentado", Toast.LENGTH_LONG).show();

        // Ajustar el edittext de producto
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, TipoProductos);
        TipoAC.setAdapter(adapter);

        // Creamos un Runnable que se encargará de actualizar el contenido del EditText
        // cada 0.5 segundo (500 milisegundos)
        runnable = new Runnable() {
            @Override
            public void run() {
                // Obtener el Valor de la presentacion
                String TipoS = TipoAC.getText().toString();
                String MarcaS = MarcaAC.getText().toString();
                String NombreS = NombreAC.getText().toString();
                String PresentacionS = PresentacionAC.getText().toString();

                String Ref;
                String Des;

                // Realiza la acción después de 1/2 segundo
                try {
                    Visuales(TipoAC, MarcaAC, NombreAC, PresentacionAC);
                    // Ajustes del EditText de producto
                    TipoET(TipoAC, MarcaAC, NombreAC, PresentacionAC, adapter);

                    ProductoTotal = dbPC.ProductoCompleto(TipoS, MarcaS, NombreS, PresentacionS);
                    // Colocar el valor del arraylist en cada objeto visual
                    Ref = ProductoTotal.get(0).get("Referencia");
                    Des = ProductoTotal.get(0).get("Descripcion");
                    VUn = ProductoTotal.get(0).get("ValorUnitario");
                    ReferenciaTW.setText(Ref);
                    DescripcionTW.setText(Des);
                    ValorUnitarioTW.setText(NumeroAjustado(VUn));

                } catch (Exception e) {
                    ProductoTotal.clear();
                    ReferenciaTW.setText("");
                    DescripcionTW.setText("");
                    ValorUnitarioTW.setText("");
                }
                handler.postDelayed(this, 500);
            }
        };
        // Iniciamos la actualización
        handler.postDelayed(runnable, 500);

        // Agregar los botones y sus funcionalidades
        Button AgregarItem = findViewById(R.id.agregaritem);
        AgregarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // No puedo agregar un item si alguno de los campos obligatorios no esta diligenciado
                if (TipoAC.getText().toString().equals("") || MarcaAC.getText().toString().equals("") || NombreAC.getText().toString().equals("") || PresentacionAC.getText().toString().equals("") || CantidadET.getText().toString().equals("")){
                    Toast.makeText(AgregarProductos.this, "Debes diligenciar toda la información obligatoria. Revisa de nuevo todos los campos.", Toast.LENGTH_SHORT).show();
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
                    int ValorUnitarioS = Integer.valueOf(VUn);
                    int CantidadN = Integer.valueOf(CantidadET.getText().toString());
                    String ObservacionesS = ComentariosET.getText().toString();

                    ////// POR VALIDAR COMO SE APLICAN LOS DESCUENTOS
                    int ValorTotal = (ValorUnitarioS * (1 - (DescuentoS / 100))) * CantidadN;

                    // El estatus solo puede ser cambiado por la persona que alista el pedido
                    int Estatus = 0;

                    // Agregar pedido
                    dbPC.AgregarProductoLocal(CodigoPedido, ReferenciaS, TipoS, MarcaS, NombreS, PresentacionS, DescripcionS, ValorUnitarioS, CantidadN, DescuentoS, CantidadDescuentoS, ValorTotal, ObservacionesS, DatosUsuario.get(2), Estatus);
                    Toast.makeText(AgregarProductos.this, "Item agregado con exito", Toast.LENGTH_SHORT).show();

                    // Limpiar campos
                    ReferenciaTW.setText("");
                    TipoAC.setText("");
                    MarcaAC.setText("");
                    NombreAC.setText("");
                    PresentacionAC.setText("");
                    DescripcionTW.setText("");
                    ValorUnitarioTW.setText("");
                    CantidadET.setText("");
                    CantidadDescuentoET.setText("");
                    DescuentoET.setText("");
                    ComentariosET.setText("");

                }
            }
        });

        Button ResumenPedido = findViewById(R.id.tablapedido);
        ResumenPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AgregarProductos.this, ResumenProductos.class);
                intent.putExtra("CodigoPedido", CodigoPedido);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                intent.putStringArrayListExtra("InformacionCliente", ClienteC);
                startActivity(intent);
                finish();

            }
        });

        // Funcionalidad del boton de retroceso
        retroceso = findViewById(R.id.retroceder);
        retroceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgregarProductos.this, RelacionarCliente.class);
                intent.putExtra("CodigoPedido", CodigoPedido);
                intent.putStringArrayListExtra("InformacionUsuario", DatosUsuario);
                startActivity(intent);
                finish();
            }
        });

    }

    // Ajustar un valor a la moneda y el separador de miles
    public String NumeroAjustado(String Precio){

        // Convertir el precio a formato double
        double precio = Double.valueOf(Precio);

        Locale locale = new Locale("es", "CO"); // crea un Locale para el formato deseado
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale); // crea una instancia de NumberFormat para el formato de moneda
        nf.setMaximumFractionDigits(0);
        String formattedNumber = nf.format(precio); // formatea el número como una cadena con el formato de moneda

        return formattedNumber;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    // Funciones para ajustar el funcionamiento del EditText de Tipo
    public void TipoET(AutoCompleteTextView TipoPro, AutoCompleteTextView MarcaPro, AutoCompleteTextView NombrePro, AutoCompleteTextView PresentacionPro, ArrayAdapter<String> adaptador){

        // Funciones visuales
        TipoVisual(TipoPro, adaptador);

        // Funciones funcionales
        TipoFuncial(TipoPro, adaptador, MarcaPro, NombrePro, PresentacionPro);

    }

    // Funciones que se ejecutaran sobre y desde el EditText del Tipo
    public void TipoVisual(AutoCompleteTextView TipoPro, ArrayAdapter<String> adaptador) {

        // Validar que solo se puedan colocar las opciones del adaptador
        TipoPro.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if (adaptador.getPosition(text.toString()) >= 0) {
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

    // Funciones que se ejecutaran sobre y desde el EditText del Tipo
    public void TipoFuncial(AutoCompleteTextView TipoPro, ArrayAdapter<String> adaptador, AutoCompleteTextView MarcaPro, AutoCompleteTextView NombrePro, AutoCompleteTextView PresentacionPro) {

        TipoPro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence tipo, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence tipo, int start, int before, int count) {

                // Buscar en la base de datos las posibles opciones del tipo escogido
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (adaptador.getPosition(tipo.toString()) >= 0) {
                            // El valor es válido porque está en el ArrayAdapter
                            // Si es valido habilitar el edittext, si no, deshabilitarlo
                            MarcaPro.setEnabled(true);
                        } else {
                            // El valor no es válido
                            MarcaPro.setEnabled(false);
                        }

                        // Realiza la acción después de 1/2 segundo
                        try {
                            MarcaProductos = dbPC.MarcasUnicas(tipo);
                        } catch (Exception e) {
                            MarcaProductos.clear();
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AgregarProductos.this, android.R.layout.simple_dropdown_item_1line, MarcaProductos);
                        MarcaPro.setAdapter(adapter2);

                        // Funciones para ajustar el funcionamiento del EditText de Marca
                        MarcaET(MarcaPro, adapter2, tipo, NombrePro, PresentacionPro);
                    }
                }, 300);

            }

            @Override
            public void afterTextChanged(Editable tipo) {

            }
        });

    }

    // Funciones para ajustar el funcionamiento del EditText de Marca
    public void MarcaET(AutoCompleteTextView MarcaPro, ArrayAdapter<String> adaptador, CharSequence Tipo, AutoCompleteTextView NombrePro, AutoCompleteTextView PresentacionPro){

        MarcaVisual(MarcaPro, adaptador, NombrePro);
        MarcaFuncial(MarcaPro, adaptador, NombrePro, PresentacionPro, Tipo);

    }

    // Funciones que se ejecutaran sobre y desde el EditText del Marca
    public void MarcaVisual(AutoCompleteTextView MarcaPro, ArrayAdapter<String> adaptador, AutoCompleteTextView NombrePro) {

        // Validar que solo se puedan colocar las opciones del adaptador
        MarcaPro.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if (adaptador.getPosition(text.toString()) >= 0) {
                    // El valor es válido porque está en el ArrayAdapter
                    // Si es valido habilitar el edittext, si no, deshabilitarlo
                    NombrePro.setEnabled(true);
                    return true;
                } else {
                    // El valor no es válido
                    NombrePro.setEnabled(true);
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

    // Funciones que se ejecutaran sobre y desde el EditText del Marca
    public void MarcaFuncial(AutoCompleteTextView MarcaPro, ArrayAdapter<String> adaptador, AutoCompleteTextView NombrePro, AutoCompleteTextView PresentacionPro, CharSequence Tipo) {

        MarcaPro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence marca, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence marca, int start, int before, int count) {

                // Buscar en la base de datos las posibles opciones del tipo escogido
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (adaptador.getPosition(marca.toString()) >= 0) {
                            // El valor es válido porque está en el ArrayAdapter
                            // Si es valido habilitar el edittext, si no, deshabilitarlo
                            NombrePro.setEnabled(true);
                        } else {
                            // El valor no es válido
                            NombrePro.setEnabled(false);
                        }

                        // Realiza la acción después de 1/2 segundo
                        try {
                            NombreProductos = dbPC.NombresUnicos(Tipo, marca);
                        } catch (Exception e) {
                            NombreProductos.clear();
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(AgregarProductos.this, android.R.layout.simple_dropdown_item_1line, NombreProductos);
                        NombrePro.setAdapter(adapter3);

                        // Funciones para ajustar el funcionamiento del EditText de nombre
                        NombreET(NombrePro, adapter3, Tipo, PresentacionPro ,marca);
                    }
                }, 300);

            }

            @Override
            public void afterTextChanged(Editable marca) {

            }
        });

    }

    // Funciones para ajustar el funcionamiento del EditText de Marca
    public void NombreET(AutoCompleteTextView NombrePro, ArrayAdapter<String> adaptador, CharSequence Tipo, AutoCompleteTextView PresentacionPro, CharSequence Marca){

        NombreVisual(NombrePro, adaptador, PresentacionPro);
        NombreFuncial(NombrePro, adaptador, PresentacionPro, Tipo, Marca);

    }

    // Funciones que se ejecutaran sobre y desde el EditText del Nombre
    public void NombreVisual(AutoCompleteTextView NombrePro, ArrayAdapter<String> adaptador, AutoCompleteTextView PresentacionPro) {

        // Validar que solo se puedan colocar las opciones del adaptador
        NombrePro.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if (adaptador.getPosition(text.toString()) >= 0) {
                    // El valor es válido porque está en el ArrayAdapter
                    // Si es valido habilitar el edittext, si no, deshabilitarlo
                    PresentacionPro.setEnabled(true);
                    return true;
                } else {
                    // El valor no es válido
                    PresentacionPro.setEnabled(true);
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

    // Funciones que se ejecutaran sobre y desde el EditText del Nombre
    public void NombreFuncial(AutoCompleteTextView NombrePro,  ArrayAdapter<String> adaptador, AutoCompleteTextView PresentacionPro, CharSequence Tipo, CharSequence Marca) {

        NombrePro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence nombre, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence nombre, int start, int before, int count) {

                // Buscar en la base de datos las posibles opciones del tipo escogido
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (adaptador.getPosition(nombre.toString()) >= 0) {
                            // El valor es válido porque está en el ArrayAdapter
                            // Si es valido habilitar el edittext, si no, deshabilitarlo
                            PresentacionPro.setEnabled(true);
                        } else {
                            // El valor no es válido
                            PresentacionPro.setEnabled(false);
                        }
                        // Realiza la acción después de 1/2 segundo
                        try {
                            PresentacionProductos = dbPC.PresentacionesUnicas(Tipo, Marca, nombre);
                        } catch (Exception e) {
                            PresentacionProductos.clear();
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(AgregarProductos.this, android.R.layout.simple_dropdown_item_1line, PresentacionProductos);
                        PresentacionPro.setAdapter(adapter4);

                        // Funciones para ajustar el funcionamiento del EditText de nombre
                        PrsentacionET(PresentacionPro, adapter4);//, ReferenciaPro, DesciprcionPro, ValorUnitarioPro);
                    }
                }, 300);

            }

            @Override
            public void afterTextChanged(Editable nombre) {

            }
        });

    }

    // Funciones que se ejecutaran sobre y desde el EditText del Prsentacion
    public void PrsentacionET(AutoCompleteTextView PresentacionPro, ArrayAdapter<String> adaptador){//, TextView Referencia, TextView Descripcion, TextView ValorUnitario){

        PresentacionVisual(PresentacionPro, adaptador);

    }

    // Funciones que se ejecutaran sobre y desde el EditText del Prsentacion
    public void PresentacionVisual(AutoCompleteTextView PresentacionPro, ArrayAdapter<String> adaptador) {

        // Validar que solo se puedan colocar las opciones del adaptador
        PresentacionPro.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if (adaptador.getPosition(text.toString()) >= 0) {
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
    public void Visuales(AutoCompleteTextView TipoPro, AutoCompleteTextView MarcaPro, AutoCompleteTextView NombrePro, AutoCompleteTextView PresentacionPro){

        // Desplegar la lista de opciones cuando se toca el campo de texto
        TipoPro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    TipoPro.showDropDown();
                }
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        TipoPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipoPro.showDropDown();
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        MarcaPro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    MarcaPro.showDropDown();
                }
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        MarcaPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarcaPro.showDropDown();
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        NombrePro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    NombrePro.showDropDown();
                }
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        NombrePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NombrePro.showDropDown();
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        PresentacionPro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    PresentacionPro.showDropDown();
                }
            }
        });

        // Desplegar la lista de opciones cuando se toca el campo de texto
        PresentacionPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresentacionPro.showDropDown();
            }
        });

    }

}
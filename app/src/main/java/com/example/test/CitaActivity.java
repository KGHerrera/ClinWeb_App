package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.controlador.AnalizadorJSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import modelo.Cita;

public class CitaActivity extends AppCompatActivity implements CitaAdapter.CitaAdapterListener {
    private RecyclerView recyclerView;
    public CitaAdapter adapter;
    public List<Cita> citas;

    public String urlApi;

    LayoutInflater inflater;

    EditText searchBox;

    Button searchButton;

    String[] datosP;
    ArrayAdapter<String> adapterPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita);

        //urlApi = "https://clinweb.000webhostapp.com/API/";
        urlApi = "http://clinweb.000webhostapp.com/API/";

        datosP = new String[]{"Seleccionar doctor...","1 Dk. Damecio John", "2 Dra. Juana Gallos", "3 DK. Country 2"};

        adapterPersonal = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datosP);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        searchBox = findViewById(R.id.searchBox);
        searchButton = findViewById(R.id.searchButton);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Define la altura del espacio que deseas agregar al final de la lista
        int bottomSpaceHeight = getResources().getDimensionPixelSize(R.dimen.bottom_space_height);

        // Agrega la decoración al RecyclerView
        recyclerView.addItemDecoration(new BottomSpaceItemDecoration(bottomSpaceHeight));

        citas = new ArrayList<>();

        new Thread(() -> {
            String url = urlApi + "apiAndroidMostrar.php";
            String metodo = "GET";

            AnalizadorJSON analizadorJSON = new AnalizadorJSON();
            JSONObject jsonObject = analizadorJSON.peticionHTTPConsultas(url, metodo);

            try {
                if(jsonObject != null) {
                    JSONArray datos = jsonObject.getJSONArray("citas");

                    for (int i = 0; i < datos.length(); i++) {
                        int idCita = datos.getJSONObject(i).getInt("id_cita");
                        int fkPaciente = datos.getJSONObject(i).getInt("fk_paciente");
                        int fkPersonal = datos.getJSONObject(i).getInt("fk_personal");
                        int fkSala = datos.getJSONObject(i).getInt("fk_sala");
                        String fechaHora = datos.getJSONObject(i).getString("fecha_hora");
                        String motivoCita = datos.getJSONObject(i).getString("motivo_cita");

                        Cita cita = new Cita(idCita, fkPaciente, fkPersonal, fkSala, fechaHora, motivoCita);
                        citas.add(cita);
                    }

                    // Actualizar el adaptador en el hilo principal
                    runOnUiThread(() -> {
                        adapter = new CitaAdapter(citas);
                        adapter.setListener(this);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    Toast.makeText(CitaActivity.this, "No hubo respuesta del servidor :(", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();

        searchButton.setOnClickListener(view -> {
            if(!searchBox.getText().toString().equals("")){
                busquedaCitas(searchBox.getText().toString());
            }
        });



        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);
        fabAgregar.setOnClickListener(view -> mostrarFormulario());


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama para notificar que algo en el texto está a punto de cambiar
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String textoActual = charSequence.toString();
                if(textoActual.equals("")){
                    actualizarCitas();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama para notificar que algo en el texto ha cambiado
                // después de que el cambio ha ocurrido
            }
        });


        inflater = LayoutInflater.from(this);
    }


    public void mostrarConfirmacionEliminar(Cita cita, Context context) {
        AlertDialog.Builder confirmarEliminarBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        confirmarEliminarBuilder.setTitle("¿Estás seguro de eliminar esta cita?");
        confirmarEliminarBuilder.setPositiveButton("Eliminar", (dialog, which) -> {
            eliminarCita(cita);
        });
        confirmarEliminarBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog confirmarEliminarDialog = confirmarEliminarBuilder.create();
        confirmarEliminarDialog.show();
    }

    public void eliminarCita(Cita cita){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();

        if (network != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null) {

            new Thread(() -> {

                String url = urlApi + "apiAndroidBaja.php";
                String metodo = "POST";

                AnalizadorJSON analizadorJSON = new AnalizadorJSON();
                JSONObject jsonObject = analizadorJSON.realizarEliminacion(url, metodo, cita.getId());


                try {
                    if(jsonObject != null) {
                        String mensaje = jsonObject.getString("mensaje");
                        boolean exito = mensaje.equals("Cita eliminada correctamente");

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

                            if (exito) {
                                actualizarCitas();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No hubo respuesta del servidor :(", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }).start();

        } else
            Log.e("MSJ->", "Error en la red");
    }


    public void modificarCita(Cita cita){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();

        if (network != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null) {
            new Thread(() -> {
                String url = urlApi + "apiAndroidCambios.php";
                String metodo = "POST";

                AnalizadorJSON analizadorJSON = new AnalizadorJSON();
                JSONObject jsonObject = analizadorJSON.realizarCambio(url, metodo, cita);

                try {
                    if(jsonObject != null){
                        String mensaje = jsonObject.getString("mensaje");
                        boolean exito = mensaje.equals("Cita actualizada correctamente");

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                            if(exito){
                                actualizarCitas();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No hubo respuesta del servidor :(", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } else
            Log.e("MSJ->", "Error en la red");
    }

    private void mostrarFormularioEditar(Cita cita, Context context) {

        View formularioView = inflater.inflate(R.layout.formulario, null);
        EditText editTextMotivoCita = formularioView.findViewById(R.id.editTextMotivoCita);
        DatePicker datePicker = formularioView.findViewById(R.id.datePicker);
        TimePicker timePicker = formularioView.findViewById(R.id.timePicker);

        Spinner spinnerPersonal = formularioView.findViewById(R.id.spinnerPersonal);
        spinnerPersonal.setAdapter(adapterPersonal);

        int idPersonal = cita.getPersonalId();
        spinnerPersonal.setSelection(idPersonal);

        editTextMotivoCita.setText(cita.getMotivoCita());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date fechaHora = dateFormat.parse(cita.getFechaHora());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaHora);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePicker.init(year, month, day, null);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setView(formularioView)
                .setTitle("Editar Cita")
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String nuevoMotivo = editTextMotivoCita.getText().toString();

                    if(nuevoMotivo.trim().equals("")){
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Error al agregar debes escribir un motivo", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    if(spinnerPersonal.getSelectedItem().equals("Seleccionar doctor...")) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Error al agregar debes seleccionar un doctor", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }


                    // Obtén la nueva fecha y hora del DatePicker y TimePicker
                    int ano = datePicker.getYear();
                    int mes = datePicker.getMonth() + 1;
                    int dia = datePicker.getDayOfMonth();
                    int hora = timePicker.getHour();
                    int minuto = timePicker.getMinute();

                    int personal = Integer.parseInt(spinnerPersonal.getSelectedItem().toString().split(" ")[0]);
                    String fechaHora = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00", ano, mes, dia, hora, minuto);

                    cita.setPersonalId(personal);
                    cita.setMotivoCita(nuevoMotivo);
                    cita.setFechaHora(fechaHora);

                    modificarCita(cita);

                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void actualizarCitas() {
        new Thread(() -> {
            String url = urlApi + "apiAndroidMostrar.php";
            String metodo = "GET";

            AnalizadorJSON analizadorJSON = new AnalizadorJSON();
            JSONObject jsonObject = analizadorJSON.peticionHTTPConsultas(url, metodo);

            try {

                if(jsonObject != null) {
                    JSONArray datos = jsonObject.getJSONArray("citas");

                    // Limpiar la lista antes de agregar nuevas citas
                    citas.clear();

                    for (int i = 0; i < datos.length(); i++) {
                        int idCita = datos.getJSONObject(i).getInt("id_cita");
                        int fkPaciente = datos.getJSONObject(i).getInt("fk_paciente");
                        int fkPersonal = datos.getJSONObject(i).getInt("fk_personal");
                        int fkSala = datos.getJSONObject(i).getInt("fk_sala");
                        String fechaHora = datos.getJSONObject(i).getString("fecha_hora");
                        String motivoCita = datos.getJSONObject(i).getString("motivo_cita");

                        Cita cita = new Cita(idCita, fkPaciente, fkPersonal, fkSala, fechaHora, motivoCita);
                        citas.add(cita);
                    }

                    // Notificar al adaptador sobre los cambios en el hilo principal
                    runOnUiThread(() -> {
                        if (adapter != null) {
                            adapter.setListener(this);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No hubo respuesta del servidor :(", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void busquedaCitas(String criterio) {
        new Thread(() -> {
            String url = urlApi + "apiAndroidCriterio.php";
            String metodo = "GET";

            AnalizadorJSON analizadorJSON = new AnalizadorJSON();
            JSONObject jsonObject = analizadorJSON.buscarCitas(url, metodo, criterio);

            try {
                if(jsonObject != null){
                    JSONArray datos = jsonObject.getJSONArray("citas");

                    // Limpiar la lista antes de agregar nuevas citas
                    citas.clear();

                    for (int i = 0; i < datos.length(); i++) {
                        int idCita = datos.getJSONObject(i).getInt("id_cita");
                        int fkPaciente = datos.getJSONObject(i).getInt("fk_paciente");
                        int fkPersonal = datos.getJSONObject(i).getInt("fk_personal");
                        int fkSala = datos.getJSONObject(i).getInt("fk_sala");
                        String fechaHora = datos.getJSONObject(i).getString("fecha_hora");
                        String motivoCita = datos.getJSONObject(i).getString("motivo_cita");

                        Cita cita = new Cita(idCita, fkPaciente, fkPersonal, fkSala, fechaHora, motivoCita);
                        citas.add(cita);
                    }

                    // Notificar al adaptador sobre los cambios en el hilo principal
                    runOnUiThread(() -> {
                        if (adapter != null) {
                            adapter.setListener(this);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No hubo respuesta del servidor :(", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarFormulario() {


        // Obtener referencias a los elementos del formulario
        View formularioView = inflater.inflate(R.layout.formulario, null);

        EditText editTextMotivoCita = formularioView.findViewById(R.id.editTextMotivoCita);
        DatePicker datePicker = formularioView.findViewById(R.id.datePicker);
        TimePicker timePicker = formularioView.findViewById(R.id.timePicker);

        Spinner spinnerPersonal = formularioView.findViewById(R.id.spinnerPersonal);
        spinnerPersonal.setAdapter(adapterPersonal);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(formularioView)
                .setTitle("Agregar Cita")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Obtener datos del formulario
                    String motivoCita = editTextMotivoCita.getText().toString();


                    if(motivoCita.trim().equals("")){
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Error al agregar debes escribir un motivo", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    if(spinnerPersonal.getSelectedItem().equals("Seleccionar doctor...")) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Error al agregar debes seleccionar un doctor", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    // Obtener la fecha seleccionada del DatePicker
                    int dia = datePicker.getDayOfMonth();
                    int mes = datePicker.getMonth() + 1; // Los meses en DatePicker van de 0 a 11
                    int ano = datePicker.getYear();

                    int personal = Integer.parseInt(spinnerPersonal.getSelectedItem().toString().split(" ")[0]);

                    // Obtener la hora seleccionada del TimePicker
                    int hora = timePicker.getHour();
                    int minuto = timePicker.getMinute();

                    String fechaHora = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00", ano, mes, dia, hora, minuto);

                    // Crear un objeto de tipo Cita
                    Cita nuevaCita = new Cita(0,3, personal, 9, fechaHora, motivoCita);

                    // Mostrar la información de la cita en un Toast
                    //String mensaje = "Cita guardada:\n" + nuevaCita.toString();
                    //Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

                    agregarCita(nuevaCita);


                    // Cierra el diálogo
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Manejar clic en el botón "Cancelar" o simplemente cerrar el diálogo
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void agregarCita(Cita cita) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();

        if (network != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null) {

            new Thread(() -> {

                String url = urlApi + "apiAndroidAltas.php";
                String metodo = "POST";

                AnalizadorJSON analizadorJSON = new AnalizadorJSON();
                JSONObject jsonObject = analizadorJSON.realizarAlta(url, metodo, cita);

                try {
                    if(jsonObject != null){
                        String res = jsonObject.getString("mensaje");
                        boolean exito = jsonObject.getBoolean("exito");
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();

                            if(exito){
                                actualizarCitas();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No hubo respuesta del servidor :(", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).start();

        } else
            Log.e("MSJ->", "Error en la red");
    }

    @Override
    public void onCitaEdit(Cita cita, Context context) {
        mostrarFormularioEditar(cita, context);
    }

    @Override
    public void onCitaDelete(Cita cita, Context context) {
        mostrarConfirmacionEliminar(cita, context);
    }

    class BottomSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int bottomSpaceHeight;

        public BottomSpaceItemDecoration(int bottomSpaceHeight) {
            this.bottomSpaceHeight = bottomSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = bottomSpaceHeight;
            } else {
                outRect.bottom = 0;
            }
        }
    }
}

package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        citas = new ArrayList<>();

        new Thread(() -> {
            String url = "http://192.168.1.8/clinica/API/apiAndroidMostrar.php";
            String metodo = "GET";

            AnalizadorJSON analizadorJSON = new AnalizadorJSON();
            JSONObject jsonObject = analizadorJSON.peticionHTTPConsultas(url, metodo, "");

            try {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();



        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);
        fabAgregar.setOnClickListener(view -> mostrarFormulario());
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

                String url = "http://192.168.1.8/clinica/API/apiAndroidBaja.php";
                String metodo = "POST";

                AnalizadorJSON analizadorJSON = new AnalizadorJSON();
                JSONObject jsonObject = analizadorJSON.realizarEliminacion(url, metodo, cita.getId());


                try {
                    String mensaje = jsonObject.getString("mensaje");
                    boolean exito = mensaje.equals("Cita eliminada correctamente");

                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

                        if(exito){
                            actualizarCitas();
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }).start();

        } else
            Log.e("MSJ->", "Error en la red");
    }

    private void mostrarFormularioEditar(Cita cita, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View formularioView = inflater.inflate(R.layout.formulario, null);
        EditText editTextMotivoCita = formularioView.findViewById(R.id.editTextMotivoCita);
        DatePicker datePicker = formularioView.findViewById(R.id.datePicker);
        TimePicker timePicker = formularioView.findViewById(R.id.timePicker);

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

                    // Obtén la nueva fecha y hora del DatePicker y TimePicker
                    int nuevoYear = datePicker.getYear();
                    int nuevoMonth = datePicker.getMonth();
                    int nuevoDay = datePicker.getDayOfMonth();
                    int nuevoHour = timePicker.getHour();
                    int nuevoMinute = timePicker.getMinute();

                    Calendar nuevoCalendar = Calendar.getInstance();
                    nuevoCalendar.set(nuevoYear, nuevoMonth, nuevoDay, nuevoHour, nuevoMinute);
                    Date nuevaFechaHora = nuevoCalendar.getTime();


                    Toast.makeText(context, cita.toString(), Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void actualizarCitas() {
        new Thread(() -> {
            String url = "http://192.168.1.8/clinica/API/apiAndroidMostrar.php";
            String metodo = "GET";

            AnalizadorJSON analizadorJSON = new AnalizadorJSON();
            JSONObject jsonObject = analizadorJSON.peticionHTTPConsultas(url, metodo, "");

            try {
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
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarFormulario() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View formularioView = inflater.inflate(R.layout.formulario, null);

        // Obtener referencias a los elementos del formulario
        EditText editTextMotivoCita = formularioView.findViewById(R.id.editTextMotivoCita);
        DatePicker datePicker = formularioView.findViewById(R.id.datePicker);
        TimePicker timePicker = formularioView.findViewById(R.id.timePicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(formularioView)
                .setTitle("Agregar Cita")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Obtener datos del formulario
                    String motivoCita = editTextMotivoCita.getText().toString();

                    // Obtener la fecha seleccionada del DatePicker
                    int dia = datePicker.getDayOfMonth();
                    int mes = datePicker.getMonth() + 1; // Los meses en DatePicker van de 0 a 11
                    int ano = datePicker.getYear();

                    // Obtener la hora seleccionada del TimePicker
                    int hora = timePicker.getHour();
                    int minuto = timePicker.getMinute();

                    String fechaHora = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00", ano, mes, dia, hora, minuto);

                    // Crear un objeto de tipo Cita
                    Cita nuevaCita = new Cita(0,3, 1, 9, fechaHora, motivoCita);

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

                String url = "http://192.168.1.8/clinica/API/apiAndroidAltas.php";
                String metodo = "POST";

                AnalizadorJSON analizadorJSON = new AnalizadorJSON();
                JSONObject jsonObject = analizadorJSON.realizarAlta(url, metodo, cita);

                try {
                    String res = jsonObject.getString("mensaje");

                    boolean exito = jsonObject.getBoolean("exito");

                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();

                        if(exito){
                            citas.add(cita);
                            adapter.notifyDataSetChanged();
                        }
                    });



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
}

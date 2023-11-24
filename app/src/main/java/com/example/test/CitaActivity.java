package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
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

public class CitaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CitaAdapter adapter;

    public static List<Cita> citas;

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
                    recyclerView.setAdapter(adapter);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();



        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);
        fabAgregar.setOnClickListener(view -> mostrarFormulario());
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
}

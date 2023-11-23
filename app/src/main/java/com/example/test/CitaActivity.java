package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import modelo.Cita;

public class CitaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<Cita> citaList = getCitas();
        CitaAdapter adapter = new CitaAdapter(citaList);
        recyclerView.setAdapter(adapter);


        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);
        fabAgregar.setOnClickListener(view -> mostrarFormulario());
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

                    // Crear una cadena con la información y mostrarla en un Toast
                    String mensaje = "Motivo de la cita: " + motivoCita +
                            "\nFecha: " + dia + "/" + mes + "/" + ano +
                            "\nHora: " + hora + ":" + minuto;

                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

                    // Puedes hacer lo que quieras con los datos obtenidos aquí

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




    private List<Cita> getCitas() {

        List<Cita> citas = new ArrayList<>();

        citas.add(new Cita(1, 101, 201, 301, "2023-11-11 10:00 AM", "Examen de rutina"));
        citas.add(new Cita(2, 102, 202, 302, "2023-11-12 02:30 PM", "Consulta médica"));
        citas.add(new Cita(3, 103, 203, 303, "2023-11-13 09:15 AM", "Prueba de laboratorio"));
        citas.add(new Cita(1, 101, 201, 301, "2023-11-11 10:00 AM", "Examen de rutina"));
        citas.add(new Cita(2, 102, 202, 302, "2023-11-12 02:30 PM", "Consulta médica"));
        citas.add(new Cita(3, 103, 203, 303, "2023-11-13 09:15 AM", "Prueba de laboratorio"));

        return citas;
    }
}

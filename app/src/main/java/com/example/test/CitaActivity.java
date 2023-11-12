package com.example.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(formularioView)
                .setTitle("Agregar Cita")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Manejar clic en el botón "Guardar"
                    // Puedes obtener datos del formulario utilizando formularioView.findViewById(R.id.xxx)
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

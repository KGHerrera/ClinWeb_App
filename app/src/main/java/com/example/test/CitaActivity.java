package com.example.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

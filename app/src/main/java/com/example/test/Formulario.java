package com.example.test;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.controlador.AnalizadorJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import modelo.Cita;

public class Formulario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario);

    }

    public List<String> obtenerListaIdsPacientes() {
        // Implementa la lógica para obtener la lista de ids de pacientes
        // ...

        return null; // Reemplaza esto con la lista real de ids de pacientes
    }


}


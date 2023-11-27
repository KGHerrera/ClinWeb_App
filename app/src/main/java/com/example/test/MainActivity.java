package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.controlador.AnalizadorJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modelo.Cita;

public class MainActivity extends AppCompatActivity {

    EditText editUsuario;
    EditText editContrasena;

    String urlApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlApi = "http://clinweb.000webhostapp.com/API/";

        Button loginButton = findViewById(R.id.loginButton);

        editUsuario = findViewById(R.id.usernameEditText);
        editContrasena = findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(v -> {

            String usuario = editUsuario.getText().toString();
            String contrasena = editContrasena.getText().toString();

            new Thread(() -> {
                String url = urlApi + "verificarUsuario.php";
                String metodo = "POST";
                AnalizadorJSON analizadorJSON = new AnalizadorJSON();
                JSONObject jsonObject = analizadorJSON.verificarUsuario(url, metodo, usuario, contrasena);

                try {
                    boolean exito = jsonObject.getBoolean("exito");
                    if (exito) {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this, CitaActivity.class);
                            startActivity(intent);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}
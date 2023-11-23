package com.example.test.controlador;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import modelo.Cita;

public class AnalizadorJSON {

    private InputStream is = null;
    private OutputStream os = null;
    private JSONObject jsonObject = null;

    private HttpURLConnection conexion = null;
    private URL url = null;

    public JSONObject peticionHTTP(String direccionURL, String metodo, Cita cita) {
        try {
            String id = URLEncoder.encode(String.valueOf(cita.getId()), "UTF-8");
            String pacienteId = URLEncoder.encode(String.valueOf(cita.getPacienteId()), "UTF-8");
            String personalId = URLEncoder.encode(String.valueOf(cita.getPersonalId()), "UTF-8");
            String salaId = URLEncoder.encode(String.valueOf(cita.getSalaId()), "UTF-8");
            String fechaHora = URLEncoder.encode(cita.getFechaHora(), "UTF-8");
            String motivoCita = URLEncoder.encode(cita.getMotivoCita(), "UTF-8");

            JSONObject jsonCita = new JSONObject();
            jsonCita.put("fk_paciente", pacienteId);
            jsonCita.put("fk_personal", personalId);
            jsonCita.put("fk_sala", salaId);
            jsonCita.put("fecha_hora", fechaHora);
            jsonCita.put("motivo_cita", motivoCita);

            String cadenaJSON = jsonCita.toString();

            Log.i("MSJ", cadenaJSON);

            url = new URL(direccionURL);
            conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoOutput(true);
            conexion.setRequestMethod(metodo);
            conexion.setFixedLengthStreamingMode(cadenaJSON.length());
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            os = new BufferedOutputStream(conexion.getOutputStream());
            os.write(cadenaJSON.getBytes());
            os.flush();
            os.close();
        } catch (UnsupportedEncodingException | JSONException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is = new BufferedInputStream(conexion.getInputStream());
            BufferedReader br =  new BufferedReader(new InputStreamReader(is));
            StringBuilder cadena = new StringBuilder();

            String fila;
            while ((fila=br.readLine()) != null){
                cadena.append(fila).append("\n");
            }

            is.close();

            Log.i("MSJ->", String.valueOf(cadena));

            jsonObject = new JSONObject(String.valueOf(cadena));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    public JSONObject peticionHTTPConsultas(String cadenaURL, String metodo, String criterio) {
        try {
            JSONObject jsonFiltro = new JSONObject();
            jsonFiltro.put("criterio", criterio);

            String cadenaJSON = jsonFiltro.toString();

            Log.i("MSJ", cadenaJSON);

            url = new URL(cadenaURL);
            conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoOutput(true);
            conexion.setRequestMethod(metodo);
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            os = new BufferedOutputStream(conexion.getOutputStream());
            os.write(cadenaJSON.getBytes());
            os.flush();
            os.close();

        } catch (UnsupportedEncodingException | JSONException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is = new BufferedInputStream(conexion.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder cad = new StringBuilder();

            String fila;
            while ((fila = br.readLine()) != null) {
                cad.append(fila).append("\n");
            }
            is.close();

            String cadena = cad.toString();
            Log.d("--->", cadena);
            jsonObject = new JSONObject(cadena);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    
    public ArrayList<Cita> obtenerListaCitas(String direccionURL, String metodo) {
        ArrayList<Cita> listaCitas = new ArrayList<>();

        try {
            url = new URL(direccionURL);
            conexion = (HttpURLConnection) url.openConnection();

            conexion.setRequestMethod(metodo);
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            is = new BufferedInputStream(conexion.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder cad = new StringBuilder();

            String fila;
            while ((fila = br.readLine()) != null) {
                cad.append(fila).append("\n");
            }
            is.close();

            String cadena = cad.toString();
            Log.d("--->", cadena);

            // Procesar el JSON y convertirlo a ArrayList de citas
            JSONObject jsonResponse = new JSONObject(cadena);
            JSONArray jsonArray = jsonResponse.getJSONArray("citas");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCita = jsonArray.getJSONObject(i);
                Cita cita = new Cita(
                        jsonCita.getInt("id_cita"),
                        jsonCita.getInt("fk_paciente"),
                        jsonCita.getInt("fk_personal"),
                        jsonCita.getInt("fk_sala"),
                        jsonCita.getString("fecha_hora"),
                        jsonCita.getString("motivo_cita")
                );
                listaCitas.add(cita);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return listaCitas;
    }

}


package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import modelo.Cita;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {
    private List<Cita> citaList;

    public CitaAdapter(List<Cita> citaList) {
        this.citaList = citaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_cita, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = citaList.get(position);

        // Configuración de la tarjeta con los datos de la cita
        holder.fechaHoraTextView.setText("Fecha y Hora: " + cita.getFechaHora());
        holder.motivoCitaTextView.setText("Motivo: " + cita.getMotivoCita());

        // Mostrar número de sala y ID del personal
        holder.numSalaTextView.setText("Sala: " + cita.getSalaId());
        holder.idPersonalTextView.setText("ID Personal: " + cita.getPersonalId());

        // Configuración del clic del botón de editar
        holder.btnEditar.setOnClickListener(view -> mostrarFormularioEditar(cita, view.getContext()));
    }

    @Override
    public int getItemCount() {
        return citaList.size();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

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

                    // Crea un nuevo objeto Date con la nueva fecha y hora
                    Calendar nuevoCalendar = Calendar.getInstance();
                    nuevoCalendar.set(nuevoYear, nuevoMonth, nuevoDay, nuevoHour, nuevoMinute);
                    Date nuevaFechaHora = nuevoCalendar.getTime();

                    // Aquí puedes realizar la actualización de la cita en tu base de datos o donde sea necesario
                    // Llama a un método en tu actividad o fragmento para manejar la actualización
                    Toast.makeText(context, cita.toString(), Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fechaHoraTextView;
        public TextView motivoCitaTextView;
        public TextView numSalaTextView;
        public TextView idPersonalTextView;
        public Button btnEditar; // Agregado

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaHoraTextView = itemView.findViewById(R.id.fechaHoraTextView);
            motivoCitaTextView = itemView.findViewById(R.id.motivoCitaTextView);
            numSalaTextView = itemView.findViewById(R.id.numSalaTextView);
            idPersonalTextView = itemView.findViewById(R.id.idPersonalTextView);
            btnEditar = itemView.findViewById(R.id.btnEditar); // Agregado
        }
    }
}

package com.example.test;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import modelo.Cita;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    public interface CitaAdapterListener {
        void onCitaEdit(Cita cita, Context context);
        void onCitaDelete(Cita cita, Context context);
    }

    private CitaAdapterListener listener; // Variable de instancia

    public void setListener(CitaAdapterListener listener) {
        this.listener = listener;
    }

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

        holder.btnEditar.setOnClickListener(view -> {
            if (listener != null) {
                // Utiliza el contexto de la vista del botón
                Context context = view.getContext();
                listener.onCitaEdit(cita, context);
            }
        });

        holder.btnEliminar.setOnClickListener(view -> {
            if (listener != null) {
                // Utiliza el contexto de la vista del botón
                Context context = view.getContext();
                listener.onCitaDelete(cita, context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return citaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fechaHoraTextView;
        public TextView motivoCitaTextView;
        public TextView numSalaTextView;
        public TextView idPersonalTextView;
        public Button btnEditar;
        public Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaHoraTextView = itemView.findViewById(R.id.fechaHoraTextView);
            motivoCitaTextView = itemView.findViewById(R.id.motivoCitaTextView);
            numSalaTextView = itemView.findViewById(R.id.numSalaTextView);
            idPersonalTextView = itemView.findViewById(R.id.idPersonalTextView);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

        }
    }
}

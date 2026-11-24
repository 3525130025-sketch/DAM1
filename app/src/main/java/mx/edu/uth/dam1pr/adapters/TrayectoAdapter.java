package mx.edu.uth.dam1pr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import mx.edu.uth.dam1pr.R;
import mx.edu.uth.dam1pr.dto.Trayecto;

public class TrayectoAdapter extends RecyclerView.Adapter<TrayectoAdapter.TrayectoViewHolder> {

    public interface OnTrayectoClickListener {
        void OnTrayectoClick(Trayecto trayecto);
    }

    private final OnTrayectoClickListener listener;
    private final List<Trayecto> trayectos;

    public TrayectoAdapter(List<Trayecto> trayectos, OnTrayectoClickListener listener) {
        this.trayectos = trayectos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrayectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_trayecto, parent, false);
        return new TrayectoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TrayectoViewHolder holder, int position) {
        Trayecto trayecto = trayectos.get(position);
        holder.tvNombre.setText(trayecto.getNombre());
        String detalle = trayecto.getPuntos() + " puntos . " + trayecto.getFecha_creacion();
        holder.tvDetalle.setText(detalle);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnTrayectoClick(trayecto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trayectos != null ? trayectos.size() : 0;
    }

    public static class TrayectoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvDetalle;

        public TrayectoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
        }
    }
}

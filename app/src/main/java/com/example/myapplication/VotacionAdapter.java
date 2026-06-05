package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Jugador;

import java.util.List;

public class VotacionAdapter extends RecyclerView.Adapter<VotacionAdapter.ViewHolder> {
    private final List<Jugador> jugadores;
    private final OnVotoListener listener;

    public interface OnVotoListener {
        void onVoto(Jugador jugador);
    }

    public VotacionAdapter(List<Jugador> jugadores, OnVotoListener listener) {
        this.jugadores = jugadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_mvp_voto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Jugador j = jugadores.get(position);
        holder.tvNombre.setText(j.getNombre());
        holder.tvGoles.setText("Goles: " + j.getGoles());
        holder.btnVotar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVoto(j);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvGoles;
        Button btnVotar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMVP);
            tvGoles = itemView.findViewById(R.id.tvGolesMVP);
            btnVotar = itemView.findViewById(R.id.btnVotarMVP);
        }
    }
}

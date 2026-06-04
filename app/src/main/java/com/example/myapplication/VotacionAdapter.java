package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VotacionAdapter extends RecyclerView.Adapter<VotacionAdapter.ViewHolder> {

    private List<String> jugadores;
    private OnVotoClickListener listener;

    public interface OnVotoClickListener {
        void onVotoClick(String nombre);
    }

    public VotacionAdapter(List<String> jugadores, OnVotoClickListener listener) {
        this.jugadores = jugadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mvp_voto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre = jugadores.get(position);
        holder.tvNombre.setText(nombre);
        holder.btnVotar.setOnClickListener(v -> listener.onVotoClick(nombre));
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        View btnVotar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreVoto);
            btnVotar = itemView.findViewById(R.id.btnVotarMVP);
        }
    }
}

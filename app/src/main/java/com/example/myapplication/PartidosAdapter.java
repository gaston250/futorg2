package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Partido;

import java.util.List;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.ViewHolder> {

    private final List<Partido> partidos;
    private final OnPartidoClickListener listener;

    public interface OnPartidoClickListener {
        void onPartidoClick(Partido partido);
    }

    public PartidosAdapter(List<Partido> partidos, OnPartidoClickListener listener) {
        this.partidos = partidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Partido p = partidos.get(position);
        holder.tvLugar.setText(p.getLugar());
        holder.tvFecha.setText(p.getFecha() + " - $" + p.getPrecio());
        holder.itemView.setOnClickListener(v -> listener.onPartidoClick(p));
    }

    @Override
    public int getItemCount() {
        return partidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLugar, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLugar = itemView.findViewById(android.R.id.text1);
            tvFecha = itemView.findViewById(android.R.id.text2);
        }
    }
}

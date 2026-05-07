package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Jugador;

import java.util.List;
import java.util.Locale;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<Jugador> jugadores;

    public RankingAdapter(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public void updateData(List<Jugador> newJugadores) {
        this.jugadores = newJugadores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jugador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Jugador jugador = jugadores.get(position);
        holder.tvRankingPos.setText(String.valueOf(position + 1));
        holder.tvNombre.setText(jugador.getNombre());
        holder.tvPosicion.setText(jugador.getPosicion());
        holder.tvGoles.setText(String.format(Locale.getDefault(), "%d Goles", jugador.getGoles()));
        holder.tvWinRate.setText(String.format(Locale.getDefault(), "WR: %.1f%%", jugador.getWinRate()));
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankingPos, tvNombre, tvPosicion, tvGoles, tvWinRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankingPos = itemView.findViewById(R.id.tvRankingPos);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPosicion = itemView.findViewById(R.id.tvPosicion);
            tvGoles = itemView.findViewById(R.id.tvGoles);
            tvWinRate = itemView.findViewById(R.id.tvWinRate);
        }
    }
}

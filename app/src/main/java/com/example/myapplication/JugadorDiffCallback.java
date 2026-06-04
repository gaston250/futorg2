package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.example.myapplication.models.Jugador;
import java.util.Objects;

public class JugadorDiffCallback extends DiffUtil.ItemCallback<Jugador> {
    @Override
    public boolean areItemsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
        return Objects.equals(oldItem.getNombre(), newItem.getNombre()) &&
               oldItem.getGoles() == newItem.getGoles() &&
               oldItem.getPartidosJugados() == newItem.getPartidosJugados() &&
               Double.compare(oldItem.getWinRate(), newItem.getWinRate()) == 0;
    }
}

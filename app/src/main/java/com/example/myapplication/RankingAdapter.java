package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Jugador;

import java.util.Locale;
import java.util.Objects;

/**
 * Optimized RankingAdapter using ListAdapter and DiffUtil.
 * Addresses Section 4: Performance and incremental updates.
 */
public class RankingAdapter extends ListAdapter<Jugador, RankingAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Jugador jugador);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RankingAdapter() {
        super(new JugadorDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_pro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Jugador jugador = getItem(position);
            if (jugador == null) return;
            
            // Puesto y Medallas
            int ranking = position + 1;
            holder.tvRank.setText(String.valueOf(ranking));
            if (ranking == 1) holder.tvRank.setTextColor(0xFFFFD700); // Oro
            else if (ranking == 2) holder.tvRank.setTextColor(0xFFC0C0C0); // Plata
            else if (ranking == 3) holder.tvRank.setTextColor(0xFFCD7F32); // Bronce
            else holder.tvRank.setTextColor(0xFF888888);

            holder.tvNombre.setText(jugador.getNombre() != null ? jugador.getNombre() : "N/A");
            holder.tvPosicion.setText(jugador.getPosicion() != null ? jugador.getPosicion() : "N/A");
            holder.tvPJ.setText(String.valueOf(jugador.getPartidosJugados()));
            holder.tvGoles.setText(String.valueOf(jugador.getGoles()));
            holder.tvWinRate.setText(String.format(Locale.getDefault(), "%.0f%%", jugador.getWinRate()));

            // Resaltar Win Rate en verde
            holder.tvWinRate.setTextColor(0xFF00FF7F);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null && holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(jugador);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvNombre, tvPosicion, tvPJ, tvGoles, tvWinRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRankNum);
            tvNombre = itemView.findViewById(R.id.tvRankNombre);
            tvPosicion = itemView.findViewById(R.id.tvRankPos);
            tvPJ = itemView.findViewById(R.id.tvRankPJ);
            tvGoles = itemView.findViewById(R.id.tvRankGoles);
            tvWinRate = itemView.findViewById(R.id.tvRankWR);
        }
    }
}

package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Jugador;
import com.example.myapplication.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class AmigosAdapter extends ListAdapter<Jugador, AmigosAdapter.ViewHolder> {

    private List<Jugador> amigosFull = new ArrayList<>();
    private OnAddFriendListener addFriendListener;

    public interface OnAddFriendListener {
        void onAddFriend(Jugador jugador);
    }

    public AmigosAdapter(OnAddFriendListener listener) {
        super(new JugadorDiffCallback());
        this.addFriendListener = listener;
    }

    public void setAmigosFull(List<Jugador> list) {
        this.amigosFull = new ArrayList<>(list);
        submitList(list);
    }

    public void filter(String text) {
        if (text.isEmpty()) {
            submitList(amigosFull);
        } else {
            List<Jugador> filteredList = new ArrayList<>();
            String filterPattern = text.toLowerCase().trim();
            for (Jugador item : amigosFull) {
                if (item.getNombre() != null && item.getNombre().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
            submitList(filteredList);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amigo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Jugador amigo = getItem(position);
        if (amigo == null) return;

        holder.tvNombre.setText(amigo.getNombre());
        String nivelStr = "Nivel: " + (amigo.getNivel() == 1 ? "Bajo" : amigo.getNivel() == 3 ? "Alto" : "Medio");
        holder.tvNivel.setText(nivelStr);
        
        // WhatsApp Invite
        holder.btnWhatsApp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp");
            intent.putExtra(Intent.EXTRA_TEXT, "¡Sumate a jugar! Descarga la app futorg: futorg://partido/open");
            try {
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                UiUtils.mostrarToast(v.getContext(), "WhatsApp no instalado");
            }
        });

        // Add / Invite via App
        holder.btnApp.setOnClickListener(v -> {
            if (addFriendListener != null) {
                addFriendListener.onAddFriend(amigo);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvNivel;
        ImageButton btnWhatsApp, btnApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvAmigoNombre);
            tvNivel = itemView.findViewById(R.id.tvAmigoNivel);
            btnWhatsApp = itemView.findViewById(R.id.btnInvitarWhatsApp);
            btnApp = itemView.findViewById(R.id.btnInvitarApp);
        }
    }

    private static class JugadorDiffCallback extends androidx.recyclerview.widget.DiffUtil.ItemCallback<Jugador> {
        @Override
        public boolean areItemsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
            return java.util.Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
            return oldItem.getGoles() == newItem.getGoles() &&
                   java.util.Objects.equals(oldItem.getNombre(), newItem.getNombre());
        }
    }
}

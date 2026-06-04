package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfirmadosAdapter extends ListAdapter<String, ConfirmadosAdapter.ViewHolder> {

    private final Set<String> pagados = new HashSet<>();
    private final boolean isAdmin;
    private final OnPagoChangeListener pagoListener;

    public interface OnPagoChangeListener {
        void onPagoChanged(String nombreJugador, boolean pagado, int totalPagados);
    }

    public ConfirmadosAdapter(boolean isAdmin, OnPagoChangeListener listener) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.isAdmin = isAdmin;
        this.pagoListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirmado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre = getItem(position);
        holder.tvNombre.setText(nombre);
        
        boolean estaPagado = pagados.contains(nombre);
        
        // Usar íconos estándar de Android para el estado de pago
        holder.ivPago.setImageResource(estaPagado ? android.R.drawable.ic_menu_save : android.R.drawable.ic_menu_help);
        holder.ivPago.setColorFilter(estaPagado ? 0xFF00FF7F : 0xFF888888); // Verde neón si pagó, gris si no.

        if (isAdmin) {
            holder.itemView.setOnClickListener(v -> {
                boolean nuevoEstado = !estaPagado;
                if (nuevoEstado) pagados.add(nombre);
                else pagados.remove(nombre);
                
                notifyItemChanged(holder.getBindingAdapterPosition());
                if (pagoListener != null) {
                    pagoListener.onPagoChanged(nombre, nuevoEstado, pagados.size());
                }
            });
        }
    }

    public void setPagados(List<String> nombresPagados) {
        this.pagados.clear();
        if (nombresPagados != null) {
            this.pagados.addAll(nombresPagados);
        }
        notifyDataSetChanged();
    }

    public void resetPagos() {
        pagados.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivPago;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreConfirmado);
            ivPago = itemView.findViewById(R.id.ivAvatar); // Reutilizamos el espacio del avatar para el ícono de pago
        }
    }
}

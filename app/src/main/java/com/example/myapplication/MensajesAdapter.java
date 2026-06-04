package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Mensaje;

import java.util.List;
import java.util.Objects;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {

    private final List<Mensaje> mensajes;
    private String currentUserName = "";

    public MensajesAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public void setCurrentUserName(String name) {
        this.currentUserName = name;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_realistic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje msg = mensajes.get(position);
        
        holder.tvNombre.setText(msg.getJugadorNombre());
        holder.tvTexto.setText(msg.getTexto());
        holder.tvAvatar.setText(msg.getAvatarInicial());

        boolean esMio = msg.getJugadorNombre() != null && Objects.equals(msg.getJugadorNombre(), currentUserName);
        
        RelativeLayout.LayoutParams bubbleParams = (RelativeLayout.LayoutParams) holder.llBubbleContainer.getLayoutParams();
        RelativeLayout.LayoutParams avatarParams = (RelativeLayout.LayoutParams) holder.cvAvatar.getLayoutParams();

        if (esMio) {
            // Mis mensajes a la derecha
            bubbleParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            bubbleParams.removeRule(RelativeLayout.RIGHT_OF);
            bubbleParams.addRule(RelativeLayout.LEFT_OF, 0); 
            
            avatarParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            avatarParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            
            bubbleParams.setMarginEnd(40); 
            
            holder.llBubbleContainer.setBackgroundResource(R.drawable.bg_chat_input);
        } else {
            // Mensajes de otros a la izquierda
            bubbleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            bubbleParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            bubbleParams.addRule(RelativeLayout.RIGHT_OF, R.id.cvAvatar);
            
            avatarParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            avatarParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            
            bubbleParams.setMarginEnd(0);
        }
        
        holder.llBubbleContainer.setLayoutParams(bubbleParams);
        holder.cvAvatar.setLayoutParams(avatarParams);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTexto, tvAvatar;
        LinearLayout llBubbleContainer;
        View cvAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMsg);
            tvTexto = itemView.findViewById(R.id.tvTextoMsg);
            tvAvatar = itemView.findViewById(R.id.tvAvatarMsg);
            llBubbleContainer = itemView.findViewById(R.id.llBubbleContainer);
            cvAvatar = itemView.findViewById(R.id.cvAvatar);
        }
    }
}

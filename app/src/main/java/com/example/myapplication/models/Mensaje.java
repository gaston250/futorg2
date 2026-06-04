package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Mensaje {
    @SerializedName("id")
    private Integer id;

    @SerializedName("remitente")
    private String jugadorNombre;

    @SerializedName("contenido")
    private String texto;

    @SerializedName("partido_id")
    private int partidoId;

    @SerializedName("tipo")
    private String tipoMensaje = "texto";

    @SerializedName("avatar_inicial")
    private String avatarInicial;

    @SerializedName("is_stat_card")
    private boolean isStatCard;

    @SerializedName("created_at")
    private String createdAt;

    private boolean isSent = false;

    public Mensaje() {}

    // Constructor para mensajes normales
    public Mensaje(String jugadorNombre, String texto, int partidoId) {
        this.jugadorNombre = jugadorNombre;
        this.texto = texto;
        this.partidoId = partidoId;
        this.tipoMensaje = "texto";
        this.avatarInicial = jugadorNombre.substring(0, 1).toUpperCase();
        this.isSent = true;
    }

    // Constructor para respuestas rápidas
    public Mensaje(String jugadorNombre, String texto, int partidoId, String tipo) {
        this(jugadorNombre, texto, partidoId);
        this.tipoMensaje = tipo;
    }

    // Getters
    public String getJugadorNombre() { return jugadorNombre; }
    public String getTexto() { return texto; }
    public int getPartidoId() { return partidoId; }
    public String getTipoMensaje() { return tipoMensaje; }
    public String getAvatarInicial() { return avatarInicial; }
    public String getCreatedAt() { return createdAt; }
    public boolean isSent() { return isSent; }
}

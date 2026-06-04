package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Voto {
    @SerializedName("partido_id")
    private int partidoId;

    @SerializedName("voto_jugador_nombre")
    private String votoJugadorNombre;

    @SerializedName("voto_por_nombre")
    private String votoPorNombre;

    public Voto() {}

    public Voto(int partidoId, String votoJugadorNombre, String votoPorNombre) {
        this.partidoId = partidoId;
        this.votoJugadorNombre = votoJugadorNombre;
        this.votoPorNombre = votoPorNombre;
    }

    public int getPartidoId() { return partidoId; }
    public void setPartidoId(int partidoId) { this.partidoId = partidoId; }

    public String getVotoJugadorNombre() { return votoJugadorNombre; }
    public void setVotoJugadorNombre(String votoJugadorNombre) { this.votoJugadorNombre = votoJugadorNombre; }

    public String getVotoPorNombre() { return votoPorNombre; }
    public void setVotoPorNombre(String votoPorNombre) { this.votoPorNombre = votoPorNombre; }
}

package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Voto {
    @SerializedName("partido_id")
    private int partidoId;

    @SerializedName("voto_jugador_nombre")
    private String votoJugadorNombre;

    @SerializedName("voto_por_nombre")
    private String votoPorNombre;

    public Voto(int partidoId, String votoJugadorNombre, String votoPorNombre) {
        this.partidoId = partidoId;
        this.votoJugadorNombre = votoJugadorNombre;
        this.votoPorNombre = votoPorNombre;
    }
}

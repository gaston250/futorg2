package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class PartidoContainer {
    @SerializedName("partidos")
    private Partido partido;

    public Partido getPartido() {
        return partido;
    }
}

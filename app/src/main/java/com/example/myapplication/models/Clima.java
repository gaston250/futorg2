package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Clima {
    @SerializedName("id")
    private int id;
    
    @SerializedName("temperatura")
    private double temperatura;
    
    @SerializedName("viento")
    private double viento;
    
    @SerializedName("lluvia_prob")
    private double lluviaProb;
    
    @SerializedName("descripcion")
    private String descripcion;
    
    @SerializedName("fecha")
    private String fecha;

    public Clima(double temperatura, double viento, double lluviaProb, String descripcion, String fecha) {
        this.temperatura = temperatura;
        this.viento = viento;
        this.lluviaProb = lluviaProb;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // Getters
    public double getTemperatura() { return temperatura; }
    public double getViento() { return viento; }
    public double getLluviaProb() { return lluviaProb; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }
}

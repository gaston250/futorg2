package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Partido {
    @SerializedName("id")
    private int id;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("hora")
    private String hora;
    
    @SerializedName("lugar")
    private String lugar;
    
    @SerializedName("precio")
    private double precio;

    public Partido() {}

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}

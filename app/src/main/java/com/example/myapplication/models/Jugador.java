package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Jugador {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("posicion")
    private String posicion;
    
    @SerializedName("goles")
    private int goles;
    
    @SerializedName("partidos_ganados")
    private int partidosGanados;
    
    @SerializedName("partidos_jugados")
    private int partidosJugados;
    
    @SerializedName("disponibilidad")
    private String disponibilidad;
    
    @SerializedName("zona")
    private String zona;

    // Constructor, getters and setters
    public Jugador() {}

    public double getWinRate() {
        if (partidosJugados == 0) return 0.0;
        return ((double) partidosGanados / partidosJugados) * 100;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }
    public int getPartidosGanados() { return partidosGanados; }
    public void setPartidosGanados(int partidosGanados) { this.partidosGanados = partidosGanados; }
    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }
    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
}

package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Jugador {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("foto_url")
    private String fotoUrl;
    
    @SerializedName("posicion")
    private String posicion = "Delantero";
    
    @SerializedName("nivel")
    private int nivel = 1;

    @SerializedName("disponibilidad")
    private String disponibilidad = "Disponible";

    // Atributos de habilidades (FIFA Style)
    @SerializedName("velocidad")
    private int velocidad = 50;
    
    @SerializedName("tiro")
    private int tiro = 50;
    
    @SerializedName("pase")
    private int pase = 50;
    
    @SerializedName("defensa")
    private int defensa = 50;

    // Estadísticas acumuladas
    @SerializedName("goles")
    private int goles;
    
    @SerializedName("asistencias")
    private int asistencias;
    
    @SerializedName("partidos_jugados")
    private int partidosJugados;
    
    @SerializedName("partidos_ganados")
    private int partidosGanados;

    @SerializedName("mvp_count")
    private int mvpCount;

    @SerializedName("fairplay")
    private int fairplay = 100;

    @SerializedName("pagado")
    private boolean pagado;

    public Jugador() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    // Getters Profesionales
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }
    
    // Getters de habilidades
    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
    public int getTiro() { return tiro; }
    public void setTiro(int tiro) { this.tiro = tiro; }
    public int getPase() { return pase; }
    public void setPase(int pase) { this.pase = pase; }
    public int getDefensa() { return defensa; }
    public void setDefensa(int defensa) { this.defensa = defensa; }

    // Getters de estadísticas
    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }
    public int getAsistencias() { return asistencias; }
    public void setAsistencias(int asistencias) { this.asistencias = asistencias; }
    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }
    public int getPartidosGanados() { return partidosGanados; }
    public void setPartidosGanados(int partidosGanados) { this.partidosGanados = partidosGanados; }

    public int getMvpCount() { return mvpCount; }
    public void setMvpCount(int mvpCount) { this.mvpCount = mvpCount; }
    public int getFairplay() { return fairplay; }
    public void setFairplay(int fairplay) { this.fairplay = fairplay; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }

    public double getWinRate() {
        return (partidosJugados <= 0) ? 0 : ((double) partidosGanados * 100 / (double) partidosJugados);
    }

    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() && email != null && email.contains("@");
    }
}

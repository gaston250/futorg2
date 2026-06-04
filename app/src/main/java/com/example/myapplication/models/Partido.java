package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Partido {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("hora")
    private String hora;
    
    @SerializedName("lugar")
    private String lugar;
    
    @SerializedName("precio")
    private double precio;
    
    @SerializedName("max_jugadores")
    private int maxJugadores = 10;
    
    @SerializedName("alias_pago")
    private String aliasPago;
    
    @SerializedName("is_closed")
    private Boolean isClosed = false;

    @SerializedName("organizador_id")
    private String organizadorId;

    @SerializedName("jugadores")
    private List<Integer> jugadoresIds;

    @SerializedName("estado")
    private String estado = "pendiente"; // pendiente, en_juego, finalizado

    @SerializedName("equipo_a_goles")
    private int equipoAGoles;

    @SerializedName("equipo_b_goles")
    private int equipoBGoles;

    @SerializedName("arbitro_id")
    private Integer arbitroId;

    public Partido() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getMaxJugadores() { return maxJugadores; }
    public void setMaxJugadores(int maxJugadores) { this.maxJugadores = maxJugadores; }

    public String getAliasPago() { return aliasPago; }
    public void setAliasPago(String aliasPago) { this.aliasPago = aliasPago; }

    public Boolean isClosed() { return isClosed; }
    public void setClosed(Boolean closed) { isClosed = closed; }

    public String getOrganizadorId() { return organizadorId; }
    public void setOrganizadorId(String organizadorId) { this.organizadorId = organizadorId; }

    public List<Integer> getJugadoresIds() { return jugadoresIds; }
    public void setJugadoresIds(List<Integer> jugadoresIds) { this.jugadoresIds = jugadoresIds; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getEquipoAGoles() { return equipoAGoles; }
    public void setEquipoAGoles(int equipoAGoles) { this.equipoAGoles = equipoAGoles; }

    public int getEquipoBGoles() { return equipoBGoles; }
    public void setEquipoBGoles(int equipoBGoles) { this.equipoBGoles = equipoBGoles; }

    public Integer getArbitroId() { return arbitroId; }
    public void setArbitroId(Integer arbitroId) { this.arbitroId = arbitroId; }

    public boolean isValid() {
        return fecha != null && hora != null && lugar != null && !lugar.trim().isEmpty() && precio >= 0;
    }
}

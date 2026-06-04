package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Usuario {
    @SerializedName("id")
    private String id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("foto_url")
    private String fotoUrl;
    
    @SerializedName("zona")
    private String zona;
    
    @SerializedName("disponibilidad")
    private String disponibilidad;
    
    @SerializedName("fecha_creacion")
    private Date fechaCreacion;

    public Usuario() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isValid() {
        return email != null && email.contains("@") && nombre != null && !nombre.trim().isEmpty();
    }
}

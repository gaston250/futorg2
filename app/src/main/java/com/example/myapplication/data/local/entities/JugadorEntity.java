package com.example.myapplication.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.myapplication.models.Jugador;

@Entity(tableName = "jugadores")
public class JugadorEntity {
    @PrimaryKey
    public int id;
    public String nombre;
    public String email;
    public String posicion;
    public int nivel;
    public int goles;
    public int partidosJugados;
    public double winRate;

    public JugadorEntity() {}

    public static JugadorEntity fromModel(Jugador model) {
        if (model == null || model.getId() == null) return null;
        JugadorEntity entity = new JugadorEntity();
        entity.id = model.getId();
        entity.nombre = model.getNombre();
        entity.email = model.getEmail();
        entity.posicion = model.getPosicion();
        entity.nivel = model.getNivel();
        entity.goles = model.getGoles();
        entity.partidosJugados = model.getPartidosJugados();
        entity.winRate = model.getWinRate();
        return entity;
    }

    public Jugador toModel() {
        Jugador model = new Jugador();
        model.setId(this.id);
        model.setNombre(this.nombre);
        model.setEmail(this.email);
        model.setPosicion(this.posicion);
        model.setNivel(this.nivel);
        model.setGoles(this.goles);
        model.setPartidosJugados(this.partidosJugados);
        // Note: winRate is calculated in model, but we can store it for easy access if needed
        return model;
    }
}

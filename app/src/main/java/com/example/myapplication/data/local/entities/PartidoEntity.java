package com.example.myapplication.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.myapplication.models.Partido;

@Entity(tableName = "partidos")
public class PartidoEntity {
    @PrimaryKey
    public int id;
    public String fecha;
    public String hora;
    public String lugar;
    public double precio;
    public int maxJugadores;
    public String aliasPago;
    public boolean isClosed;
    public String organizadorId;
    public String estado;

    public PartidoEntity() {}

    public static PartidoEntity fromModel(Partido model) {
        if (model == null || model.getId() == null) return null;
        PartidoEntity entity = new PartidoEntity();
        entity.id = model.getId();
        entity.fecha = model.getFecha();
        entity.hora = model.getHora();
        entity.lugar = model.getLugar();
        entity.precio = model.getPrecio();
        entity.maxJugadores = model.getMaxJugadores();
        entity.aliasPago = model.getAliasPago();
        entity.isClosed = model.isClosed() != null && model.isClosed();
        entity.organizadorId = model.getOrganizadorId();
        entity.estado = model.getEstado();
        return entity;
    }

    public Partido toModel() {
        Partido model = new Partido();
        model.setId(this.id);
        model.setFecha(this.fecha);
        model.setHora(this.hora);
        model.setLugar(this.lugar);
        model.setPrecio(this.precio);
        model.setMaxJugadores(this.maxJugadores);
        model.setAliasPago(this.aliasPago);
        model.setClosed(this.isClosed);
        model.setOrganizadorId(this.organizadorId);
        model.setEstado(this.estado);
        return model;
    }
}

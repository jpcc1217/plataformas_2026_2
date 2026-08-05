package com.farmacia.taller.v1.model;
import java.util.Date;

import jakarta.persistence.Id;
import jakarta.persistence.Entity; 

@Entity
public class Medicamento {

    @Id
    private Long id;

    private String nombre;
    private Date fechaExpedicion;
    private double precio;
    private int cantidad;

    public Medicamento() {
    }

    public Medicamento(Long id, String nombre, Date fechaExpedicion, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.fechaExpedicion = fechaExpedicion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(Date fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
}

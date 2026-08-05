package com.farmacia.taller.v1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@NoArgsConstructor @Getter @Setter
public class Categoria {

    @Id
    private Long id;
    private String nombre;
    private String descripcion;

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
}

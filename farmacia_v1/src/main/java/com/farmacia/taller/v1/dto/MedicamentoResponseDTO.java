package com.farmacia.taller.v1.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.Date;

@Getter @Setter @AllArgsConstructor
public class MedicamentoResponseDTO {
    private String nombre;
    private Date fechaExpedicion;
    private double precio;
    private int cantidad;
    private Long categoriaId;
}

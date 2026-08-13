package com.farmacia.taller.v1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class MedicamentoRequestDTO {

    @NotBlank(message = "El nombre del medicamento no puede estar vacío")
    private String nombre;

    @NotNull(message = "La fecha de expedición no puede estar vacía")
    private Date fechaExpedicion;

    @Min(value = 1, message = "El precio del medicamento no puede ser negativo")
    private double precio;

    @Min(value = 0, message = "La cantidad del medicamento no puede ser negativa")
    private int cantidad;

    @NotNull(message = "La categoría del medicamento no puede estar vacía")
    private Long categoriaId;
    
}

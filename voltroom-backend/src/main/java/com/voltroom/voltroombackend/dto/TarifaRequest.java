package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TarifaRequest {

    @NotBlank
    private String nombre;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precioPorKwh;

    @NotNull
    private LocalDate fechaInicio;

    private LocalDate fechaFin;
    private Boolean activa;
    private String descripcion;
}
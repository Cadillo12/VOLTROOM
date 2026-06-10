package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MantenimientoRequest {

    private Long incidenciaId;
    private Long tecnicoId;

    @NotBlank
    private String titulo;

    private String detalle;

    @NotNull
    @FutureOrPresent
    private LocalDateTime fechaProgramada;

    private BigDecimal costo;
    private Long creadoPor;
}
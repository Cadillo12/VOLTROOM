package com.voltroom.voltroombackend.dto;

import com.voltroom.voltroombackend.enums.AlertaNivel;
import com.voltroom.voltroombackend.enums.AlertaTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertaRequest {

    private Long ambienteId;
    private Long sensorId;

    @NotNull
    private AlertaTipo tipoAlerta;

    private AlertaNivel nivel;

    @NotBlank
    private String titulo;

    @NotBlank
    private String mensaje;

    private BigDecimal valorDetectado;
    private BigDecimal umbralReferencia;
}
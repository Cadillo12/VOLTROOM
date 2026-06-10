package com.voltroom.voltroombackend.dto;

import com.voltroom.voltroombackend.enums.IncidenciaPrioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidenciaRequest {

    private Long ambienteId;
    private Long sensorId;
    private Long reportadoPor;

    @NotBlank
    private String titulo;

    @NotBlank
    private String descripcion;

    @NotNull
    private IncidenciaPrioridad prioridad;
}
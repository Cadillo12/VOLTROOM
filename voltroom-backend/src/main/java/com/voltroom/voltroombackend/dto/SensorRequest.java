package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SensorRequest {
    @NotNull
    private Long ambienteId;

    @NotBlank
    private String codigo;

    private String tipoSensor;
    private String unidadMedida;
    private String estado;
    private LocalDate fechaInstalacion;
}

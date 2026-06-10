package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LecturaRequest {
    @NotNull
    private Long sensorId;

    @NotNull
    private LocalDateTime fechaHora;

    @NotNull
    private Double valorKwh;

    private String origenLectura;
    private String observacion;
}

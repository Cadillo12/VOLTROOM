package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AmbienteRequest {
    @NotNull
    private Long inmuebleId;

    @NotBlank
    private String nombre;

    private String tipo;
    private Integer piso;
    private String estado;
    private String descripcion;
}

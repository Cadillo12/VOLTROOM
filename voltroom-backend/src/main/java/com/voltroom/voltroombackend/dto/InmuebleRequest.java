package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InmuebleRequest {
    @NotBlank
    private String nombre;
    private String direccion;
    private String descripcion;
    private Boolean activo;
}

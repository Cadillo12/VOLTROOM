package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IncidenciaCerrarRequest {
    @NotBlank
    private String observacionCierre;
}
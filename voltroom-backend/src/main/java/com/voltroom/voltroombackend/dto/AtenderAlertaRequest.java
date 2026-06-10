package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtenderAlertaRequest {
    @NotNull
    private Long atendidaPor;
}
package com.voltroom.voltroombackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private long totalInmuebles;
    private long totalAmbientes;
    private long totalSensores;
    private long totalLecturas;
    private double consumoTotalKwh;
    private double costoEstimado;
}

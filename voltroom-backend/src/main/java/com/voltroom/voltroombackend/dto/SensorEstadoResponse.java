package com.voltroom.voltroombackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de estado en tiempo real de un sensor.
 */
@Data
@Builder
@AllArgsConstructor
public class SensorEstadoResponse {
    private Long sensorId;
    private String codigoSensor;
    private String ambienteNombre;
    private String inmuebleNombre;
    private BigDecimal kwhAcumulado;
    private BigDecimal kwhConsumoMes;
    private BigDecimal voltaje;
    private BigDecimal amperaje;
    private BigDecimal potenciaW;
    private BigDecimal costoEstimadoMes;
    private String estado;
    private LocalDateTime ultimaLectura;
}

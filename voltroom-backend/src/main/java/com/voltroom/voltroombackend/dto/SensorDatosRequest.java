package com.voltroom.voltroombackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para recibir datos enviados por el ESP32/microcontrolador.
 * El sensor envía: ID del sensor, kWh acumulado, voltaje y amperaje.
 */
@Data
public class SensorDatosRequest {
    @NotNull
    private Long sensorId;

    @NotNull
    private BigDecimal kwhActual;     // kWh acumulado del medidor

    private BigDecimal voltaje;        // Voltaje actual (V)

    private BigDecimal amperaje;       // Corriente actual (A)

    private BigDecimal potenciaW;      // Potencia actual (W)
}

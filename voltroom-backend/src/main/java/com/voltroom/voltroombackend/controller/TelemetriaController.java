package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.SensorDatosRequest;
import com.voltroom.voltroombackend.dto.SensorEstadoResponse;
import com.voltroom.voltroombackend.entity.Lectura;
import com.voltroom.voltroombackend.service.TelemetriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para la capa de telemetría.
 * 
 * Endpoints:
 *   POST /api/telemetria/datos   → Recibe datos del ESP32
 *   GET  /api/telemetria/estado  → Estado en tiempo real (para React, polling cada 5s)
 */
@RestController
@RequestMapping("/api/telemetria")
@RequiredArgsConstructor
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    /**
     * Endpoint para recibir datos JSON desde ESP32.
     * Ejemplo de payload:
     * {
     *   "sensorId": 1,
     *   "kwhActual": 125.347,
     *   "voltaje": 221.5,
     *   "amperaje": 3.2,
     *   "potenciaW": 708.8
     * }
     */
    @PostMapping("/datos")
    public ResponseEntity<Map<String, Object>> recibirDatos(
            @Valid @RequestBody SensorDatosRequest request) {
        Lectura lectura = telemetriaService.procesarDatosSensor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "OK",
                "lecturaId", lectura.getId(),
                "deltaKwh", lectura.getValorKwh(),
                "timestamp", lectura.getFechaHora().toString()
        ));
    }

    /**
     * Estado en tiempo real de todos los sensores.
     * El frontend llama este endpoint cada 5 segundos para actualizar contadores.
     */
    @GetMapping("/estado")
    public ResponseEntity<List<SensorEstadoResponse>> estadoTiempoReal() {
        return ResponseEntity.ok(telemetriaService.obtenerEstadoTiempoReal());
    }

    /**
     * Endpoint de prueba para forzar una simulación manual.
     */
    @PostMapping("/test-simular")
    public ResponseEntity<String> testSimular() {
        telemetriaService.forzarSimulacionManual();
        return ResponseEntity.ok("Simulación forzada ejecutada correctamente.");
    }
}

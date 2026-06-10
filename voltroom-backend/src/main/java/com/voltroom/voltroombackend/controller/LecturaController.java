package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.LecturaRequest;
import com.voltroom.voltroombackend.entity.Lectura;
import com.voltroom.voltroombackend.service.LecturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lecturas")
@RequiredArgsConstructor
public class LecturaController {

    private final LecturaService lecturaService;

    @GetMapping
    public ResponseEntity<List<Lectura>> listar() {
        return ResponseEntity.ok(lecturaService.listar());
    }

    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<Lectura>> listarPorSensor(@PathVariable Long sensorId) {
        return ResponseEntity.ok(lecturaService.listarPorSensor(sensorId));
    }

    @PostMapping
    public ResponseEntity<Lectura> crear(@Valid @RequestBody LecturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lecturaService.crear(request));
    }

    @GetMapping("/consumo/{sensorId}")
    public ResponseEntity<Map<String, Double>> calcularConsumo(
            @PathVariable Long sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam double tarifa) {

        double consumo = lecturaService.calcularConsumo(sensorId, inicio, fin);
        double costo = lecturaService.calcularCosto(sensorId, inicio, fin, tarifa);

        return ResponseEntity.ok(Map.of(
                "consumoKwh", consumo,
                "costoEstimado", costo
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        lecturaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.SensorRequest;
import com.voltroom.voltroombackend.entity.Sensor;
import com.voltroom.voltroombackend.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensores")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping
    public ResponseEntity<List<Sensor>> listar() {
        return ResponseEntity.ok(sensorService.listar());
    }

    @GetMapping("/ambiente/{ambienteId}")
    public ResponseEntity<List<Sensor>> listarPorAmbiente(@PathVariable Long ambienteId) {
        return ResponseEntity.ok(sensorService.listarPorAmbiente(ambienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sensor> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Sensor> crear(@Valid @RequestBody SensorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody SensorRequest request) {
        return ResponseEntity.ok(sensorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sensorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

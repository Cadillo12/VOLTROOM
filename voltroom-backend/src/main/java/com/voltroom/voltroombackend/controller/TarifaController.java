package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.TarifaRequest;
import com.voltroom.voltroombackend.entity.Tarifa;
import com.voltroom.voltroombackend.service.TarifaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService tarifaService;

    @GetMapping
    public ResponseEntity<List<Tarifa>> listar() {
        return ResponseEntity.ok(tarifaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(tarifaService.obtener(id));
    }

    @GetMapping("/activa")
    public ResponseEntity<Tarifa> obtenerActiva() {
        return ResponseEntity.ok(tarifaService.obtenerActiva());
    }

    @PostMapping
    public ResponseEntity<Tarifa> crear(@Valid @RequestBody TarifaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> actualizar(@PathVariable Long id, @Valid @RequestBody TarifaRequest request) {
        return ResponseEntity.ok(tarifaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tarifaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
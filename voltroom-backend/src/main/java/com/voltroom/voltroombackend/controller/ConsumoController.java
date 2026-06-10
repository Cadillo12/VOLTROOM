package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.entity.ConsumoResumen;
import com.voltroom.voltroombackend.service.ConsumoCalculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consumos")
@RequiredArgsConstructor
public class ConsumoController {

    private final ConsumoCalculoService consumoCalculoService;

    @GetMapping
    public ResponseEntity<List<ConsumoResumen>> listar() {
        return ResponseEntity.ok(consumoCalculoService.listarTodos());
    }

    @GetMapping("/mes-actual")
    public ResponseEntity<List<ConsumoResumen>> listarMesActual() {
        return ResponseEntity.ok(consumoCalculoService.listarMesActual());
    }

    @GetMapping("/ambiente/{id}")
    public ResponseEntity<List<ConsumoResumen>> listarPorAmbiente(@PathVariable Long id) {
        return ResponseEntity.ok(consumoCalculoService.listarPorAmbiente(id));
    }

    @PostMapping("/recalcular")
    public ResponseEntity<Map<String, String>> recalcular() {
        consumoCalculoService.calcularParaMesActual();
        return ResponseEntity.ok(Map.of("mensaje", "Consumo mensual recalculado exitosamente"));
    }
}

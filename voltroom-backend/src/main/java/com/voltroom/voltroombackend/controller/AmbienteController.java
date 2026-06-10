package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.AmbienteRequest;
import com.voltroom.voltroombackend.entity.Ambiente;
import com.voltroom.voltroombackend.service.AmbienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
@RequiredArgsConstructor
public class AmbienteController {

    private final AmbienteService ambienteService;

    @GetMapping
    public ResponseEntity<List<Ambiente>> listar() {
        return ResponseEntity.ok(ambienteService.listar());
    }

    @GetMapping("/inmueble/{inmuebleId}")
    public ResponseEntity<List<Ambiente>> listarPorInmueble(@PathVariable Long inmuebleId) {
        return ResponseEntity.ok(ambienteService.listarPorInmueble(inmuebleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ambiente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ambienteService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Ambiente> crear(@Valid @RequestBody AmbienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ambienteService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ambiente> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody AmbienteRequest request) {
        return ResponseEntity.ok(ambienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ambienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

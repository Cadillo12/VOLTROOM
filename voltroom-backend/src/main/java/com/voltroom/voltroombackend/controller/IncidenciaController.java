package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.IncidenciaCerrarRequest;
import com.voltroom.voltroombackend.dto.IncidenciaRequest;
import com.voltroom.voltroombackend.entity.Incidencia;
import com.voltroom.voltroombackend.enums.IncidenciaEstado;
import com.voltroom.voltroombackend.service.IncidenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<Incidencia>> listar() {
        return ResponseEntity.ok(incidenciaService.listar());
    }

    @GetMapping("/abiertas")
    public ResponseEntity<List<Incidencia>> listarAbiertas() {
        return ResponseEntity.ok(incidenciaService.listarAbiertas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(incidenciaService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Incidencia> crear(@Valid @RequestBody IncidenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenciaService.crear(request));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Incidencia> cambiarEstado(@PathVariable Long id,
                                                    @RequestBody Map<String, String> body) {
        IncidenciaEstado estado = IncidenciaEstado.valueOf(body.get("estado"));
        return ResponseEntity.ok(incidenciaService.cambiarEstado(id, estado));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Incidencia> cerrar(@PathVariable Long id,
                                             @Valid @RequestBody IncidenciaCerrarRequest request) {
        return ResponseEntity.ok(incidenciaService.cerrar(id, request.getObservacionCierre()));
    }
}
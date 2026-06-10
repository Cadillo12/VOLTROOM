package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.MantenimientoRequest;
import com.voltroom.voltroombackend.entity.Mantenimiento;
import com.voltroom.voltroombackend.enums.MantenimientoEstado;
import com.voltroom.voltroombackend.service.MantenimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @GetMapping
    public ResponseEntity<List<Mantenimiento>> listar() {
        return ResponseEntity.ok(mantenimientoService.listar());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Mantenimiento>> listarPorEstado(@PathVariable MantenimientoEstado estado) {
        return ResponseEntity.ok(mantenimientoService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mantenimiento> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Mantenimiento> crear(@Valid @RequestBody MantenimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimientoService.crear(request));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Mantenimiento> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.iniciar(id));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Mantenimiento> finalizar(@PathVariable Long id,
                                                   @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(mantenimientoService.finalizar(id, body.get("resultado")));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Mantenimiento> cancelar(@PathVariable Long id,
                                                  @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(mantenimientoService.cancelar(id, body.get("resultado")));
    }
}
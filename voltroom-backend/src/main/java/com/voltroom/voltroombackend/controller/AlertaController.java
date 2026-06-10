package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.AlertaRequest;
import com.voltroom.voltroombackend.dto.AtenderAlertaRequest;
import com.voltroom.voltroombackend.entity.Alerta;
import com.voltroom.voltroombackend.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public ResponseEntity<List<Alerta>> listar() {
        return ResponseEntity.ok(alertaService.listar());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Alerta>> listarPendientes() {
        return ResponseEntity.ok(alertaService.listarPendientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Alerta> crear(@Valid @RequestBody AlertaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaService.crear(request));
    }

    @PutMapping("/{id}/atender")
    public ResponseEntity<Void> atender(@PathVariable Long id,
                                           @Valid @RequestBody AtenderAlertaRequest request) {
        alertaService.atender(id, request.getAtendidaPor());
        return ResponseEntity.noContent().build();
    }
}
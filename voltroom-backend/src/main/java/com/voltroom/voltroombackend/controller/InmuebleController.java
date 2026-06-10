package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.dto.InmuebleRequest;
import com.voltroom.voltroombackend.entity.Inmueble;
import com.voltroom.voltroombackend.service.InmuebleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inmuebles")
@RequiredArgsConstructor
public class InmuebleController {

    private final InmuebleService inmuebleService;

    @GetMapping
    public ResponseEntity<List<Inmueble>> listar() {
        return ResponseEntity.ok(inmuebleService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inmueble> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(inmuebleService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inmueble> crear(@Valid @RequestBody InmuebleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inmuebleService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inmueble> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody InmuebleRequest request) {
        return ResponseEntity.ok(inmuebleService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inmuebleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

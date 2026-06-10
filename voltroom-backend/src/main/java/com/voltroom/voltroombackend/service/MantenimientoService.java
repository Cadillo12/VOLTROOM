package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.MantenimientoRequest;
import com.voltroom.voltroombackend.entity.Incidencia;
import com.voltroom.voltroombackend.entity.Mantenimiento;
import com.voltroom.voltroombackend.entity.Usuario;
import com.voltroom.voltroombackend.enums.MantenimientoEstado;
import com.voltroom.voltroombackend.repository.IncidenciaRepository;
import com.voltroom.voltroombackend.repository.MantenimientoRepository;
import com.voltroom.voltroombackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Mantenimiento> listar() {
        return mantenimientoRepository.findAll();
    }

    public List<Mantenimiento> listarPorEstado(MantenimientoEstado estado) {
        return mantenimientoRepository.findByEstado(estado);
    }

    public Mantenimiento obtener(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
    }

    public Mantenimiento crear(MantenimientoRequest request) {
        Incidencia incidencia = null;
        Usuario tecnico = null;
        Usuario creadoPor = null;

        if (request.getIncidenciaId() != null) {
            incidencia = incidenciaRepository.findById(request.getIncidenciaId())
                    .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));
        }

        if (request.getTecnicoId() != null) {
            tecnico = usuarioRepository.findById(request.getTecnicoId())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
        }

        if (request.getCreadoPor() != null) {
            creadoPor = usuarioRepository.findById(request.getCreadoPor())
                    .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));
        }

        Mantenimiento mantenimiento = Mantenimiento.builder()
                .incidencia(incidencia)
                .tecnico(tecnico)
                .titulo(request.getTitulo())
                .detalle(request.getDetalle())
                .estado(MantenimientoEstado.PROGRAMADO)
                .fechaProgramada(request.getFechaProgramada())
                .costo(request.getCosto() != null ? request.getCosto() : java.math.BigDecimal.ZERO)
                .creadoPor(creadoPor)
                .build();

        return mantenimientoRepository.save(mantenimiento);
    }

    public Mantenimiento iniciar(Long id) {
        Mantenimiento mantenimiento = obtener(id);
        mantenimiento.setEstado(MantenimientoEstado.EN_EJECUCION);
        mantenimiento.setFechaInicio(LocalDateTime.now());

        return mantenimientoRepository.save(mantenimiento);
    }

    public Mantenimiento finalizar(Long id, String resultado) {
        Mantenimiento mantenimiento = obtener(id);
        mantenimiento.setEstado(MantenimientoEstado.FINALIZADO);
        mantenimiento.setFechaFin(LocalDateTime.now());
        mantenimiento.setResultado(resultado);

        return mantenimientoRepository.save(mantenimiento);
    }

    public Mantenimiento cancelar(Long id, String resultado) {
        Mantenimiento mantenimiento = obtener(id);
        mantenimiento.setEstado(MantenimientoEstado.CANCELADO);
        mantenimiento.setResultado(resultado);

        return mantenimientoRepository.save(mantenimiento);
    }
}
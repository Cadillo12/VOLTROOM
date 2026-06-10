package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.IncidenciaRequest;
import com.voltroom.voltroombackend.entity.Ambiente;
import com.voltroom.voltroombackend.entity.Incidencia;
import com.voltroom.voltroombackend.entity.Sensor;
import com.voltroom.voltroombackend.entity.Usuario;
import com.voltroom.voltroombackend.enums.IncidenciaEstado;
import com.voltroom.voltroombackend.repository.AmbienteRepository;
import com.voltroom.voltroombackend.repository.IncidenciaRepository;
import com.voltroom.voltroombackend.repository.SensorRepository;
import com.voltroom.voltroombackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final AmbienteRepository ambienteRepository;
    private final SensorRepository sensorRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Lista todas las incidencias.
     * @return Lista de incidencias
     */
    public List<Incidencia> listar() {
        return incidenciaRepository.findAll();
    }

    public List<Incidencia> listarAbiertas() {
        return incidenciaRepository.findByEstadoIn(
                List.of(IncidenciaEstado.ABIERTA, IncidenciaEstado.EN_PROCESO)
        );
    }

    public Incidencia obtener(Long id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));
    }

    /**
     * Crea una nueva incidencia, validando previamente que el título y la descripción no estén vacíos
     * empleando Apache Commons Lang StringUtils.
     *
     * @param request Datos de la incidencia a crear.
     * @return Incidencia creada y persistida.
     */
    public Incidencia crear(IncidenciaRequest request) {
        if (org.apache.commons.lang3.StringUtils.isBlank(request.getTitulo())) {
            throw new IllegalArgumentException("El título de la incidencia no puede estar vacío");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(request.getDescripcion())) {
            throw new IllegalArgumentException("La descripción de la incidencia no puede estar vacía");
        }

        Ambiente ambiente = null;
        Sensor sensor = null;
        Usuario reportadoPor = null;

        if (request.getAmbienteId() != null) {
            ambiente = ambienteRepository.findById(request.getAmbienteId())
                    .orElseThrow(() -> new RuntimeException("Ambiente no encontrado"));
        }

        if (request.getSensorId() != null) {
            sensor = sensorRepository.findById(request.getSensorId())
                    .orElseThrow(() -> new RuntimeException("Sensor no encontrado"));
        }

        if (request.getReportadoPor() != null) {
            reportadoPor = usuarioRepository.findById(request.getReportadoPor())
                    .orElseThrow(() -> new RuntimeException("Usuario reportadoPor no encontrado"));
        }

        Incidencia incidencia = Incidencia.builder()
                .ambiente(ambiente)
                .sensor(sensor)
                .reportadoPor(reportadoPor)
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .prioridad(request.getPrioridad())
                .estado(IncidenciaEstado.ABIERTA)
                .build();

        return incidenciaRepository.save(incidencia);
    }

    public Incidencia cambiarEstado(Long id, IncidenciaEstado estado) {
        Incidencia incidencia = obtener(id);
        incidencia.setEstado(estado);

        if (estado == IncidenciaEstado.CERRADA || estado == IncidenciaEstado.RESUELTA) {
            incidencia.setFechaCierre(LocalDateTime.now());
        }

        return incidenciaRepository.save(incidencia);
    }

    public Incidencia cerrar(Long id, String observacionCierre) {
        Incidencia incidencia = obtener(id);
        incidencia.setEstado(IncidenciaEstado.CERRADA);
        incidencia.setFechaCierre(LocalDateTime.now());
        incidencia.setObservacionCierre(observacionCierre);

        return incidenciaRepository.save(incidencia);
    }
}
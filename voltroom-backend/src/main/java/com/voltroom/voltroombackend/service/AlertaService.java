package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.AlertaRequest;
import com.voltroom.voltroombackend.entity.Alerta;
import com.voltroom.voltroombackend.entity.Ambiente;
import com.voltroom.voltroombackend.entity.Sensor;
import com.voltroom.voltroombackend.entity.Usuario;
import com.voltroom.voltroombackend.repository.AlertaRepository;
import com.voltroom.voltroombackend.repository.AmbienteRepository;
import com.voltroom.voltroombackend.repository.SensorRepository;
import com.voltroom.voltroombackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AmbienteRepository ambienteRepository;
    private final SensorRepository sensorRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Alerta> listar() {
        return alertaRepository.findAll();
    }

    public List<Alerta> listarPendientes() {
        return alertaRepository.findByAtendidaFalse();
    }

    public Alerta obtener(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
    }

    public Alerta crear(AlertaRequest request) {
        Ambiente ambiente = null;
        Sensor sensor = null;

        if (request.getAmbienteId() != null) {
            ambiente = ambienteRepository.findById(request.getAmbienteId())
                    .orElseThrow(() -> new RuntimeException("Ambiente no encontrado"));
        }

        if (request.getSensorId() != null) {
            sensor = sensorRepository.findById(request.getSensorId())
                    .orElseThrow(() -> new RuntimeException("Sensor no encontrado"));
        }

        Alerta alerta = Alerta.builder()
                .ambiente(ambiente)
                .sensor(sensor)
                .tipoAlerta(request.getTipoAlerta())
                .nivel(request.getNivel() != null ? request.getNivel() : null)
                .titulo(request.getTitulo())
                .mensaje(request.getMensaje())
                .valorDetectado(request.getValorDetectado())
                .umbralReferencia(request.getUmbralReferencia())
                .atendida(false)
                .build();

        if (alerta.getNivel() == null) {
            alerta.setNivel(com.voltroom.voltroombackend.enums.AlertaNivel.MEDIO);
        }

        return alertaRepository.save(alerta);
    }

    public void atender(Long alertaId, Long usuarioId) {
        alertaRepository.deleteById(alertaId);
    }
}
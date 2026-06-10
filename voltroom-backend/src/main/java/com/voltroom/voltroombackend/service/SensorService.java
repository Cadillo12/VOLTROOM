package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.SensorRequest;
import com.voltroom.voltroombackend.entity.Ambiente;
import com.voltroom.voltroombackend.entity.Sensor;
import com.voltroom.voltroombackend.enums.SensorEstado;
import com.voltroom.voltroombackend.repository.AmbienteRepository;
import com.voltroom.voltroombackend.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final AmbienteRepository ambienteRepository;

    public List<Sensor> listar() {
        return sensorRepository.findAll();
    }

    public List<Sensor> listarPorAmbiente(Long ambienteId) {
        return sensorRepository.findByAmbienteId(ambienteId);
    }

    public Sensor obtener(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor no encontrado"));
    }

    public Sensor crear(SensorRequest request) {
        Ambiente ambiente = ambienteRepository.findById(request.getAmbienteId())
                .orElseThrow(() -> new RuntimeException("Ambiente no encontrado"));

        Sensor sensor = Sensor.builder()
                .ambiente(ambiente)
                .codigo(request.getCodigo())
                .tipoSensor(request.getTipoSensor())
                .unidadMedida(request.getUnidadMedida())
                .estado(SensorEstado.valueOf(request.getEstado()))
                .fechaInstalacion(request.getFechaInstalacion())
                .build();

        return sensorRepository.save(sensor);
    }

    public Sensor actualizar(Long id, SensorRequest request) {
        Sensor sensor = obtener(id);

        Ambiente ambiente = ambienteRepository.findById(request.getAmbienteId())
                .orElseThrow(() -> new RuntimeException("Ambiente no encontrado"));

        sensor.setAmbiente(ambiente);
        sensor.setCodigo(request.getCodigo());
        sensor.setTipoSensor(request.getTipoSensor());
        sensor.setUnidadMedida(request.getUnidadMedida());
        sensor.setEstado(SensorEstado.valueOf(request.getEstado()));
        sensor.setFechaInstalacion(request.getFechaInstalacion());

        return sensorRepository.save(sensor);
    }

    public void eliminar(Long id) {
        sensorRepository.deleteById(id);
    }
}

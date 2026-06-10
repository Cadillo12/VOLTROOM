package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.DashboardResponse;
import com.voltroom.voltroombackend.entity.Tarifa;
import com.voltroom.voltroombackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InmuebleRepository inmuebleRepository;
    private final AmbienteRepository ambienteRepository;
    private final SensorRepository sensorRepository;
    private final LecturaRepository lecturaRepository;
    private final TarifaRepository tarifaRepository;

    public DashboardResponse obtenerDashboard() {
        long totalInmuebles = inmuebleRepository.count();
        long totalAmbientes = ambienteRepository.count();
        long totalSensores = sensorRepository.count();
        long totalLecturas = lecturaRepository.count();

        double consumoTotal = lecturaRepository.findAll().stream()
                .mapToDouble(l -> l.getValorKwh() != null ? l.getValorKwh().doubleValue() : 0.0)
                .sum();

        // Usar tarifa activa real de la BD
        Optional<Tarifa> tarifaActiva = tarifaRepository.findByActivaTrue();
        double tarifaBase = tarifaActiva
                .map(t -> t.getPrecioPorKwh().doubleValue())
                .orElse(0.85);

        double costoEstimado = consumoTotal * tarifaBase;

        return new DashboardResponse(
                totalInmuebles,
                totalAmbientes,
                totalSensores,
                totalLecturas,
                consumoTotal,
                costoEstimado
        );
    }
}

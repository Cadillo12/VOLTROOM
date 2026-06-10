package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.entity.*;
import com.voltroom.voltroombackend.enums.AmbienteEstado;
import com.voltroom.voltroombackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumoCalculoService {

    private final AmbienteRepository ambienteRepository;
    private final SensorRepository sensorRepository;
    private final LecturaRepository lecturaRepository;
    private final TarifaRepository tarifaRepository;
    private final ConsumoResumenRepository consumoResumenRepository;

    /**
     * Recalcula el consumo mensual de todos los ambientes activos.
     * Se ejecuta cada 30 minutos.
     */
    @Scheduled(fixedRate = 1800000, initialDelay = 60000)
    public void recalcularConsumoMensual() {
        calcularParaMesActual();
    }

    /**
     * Método público para forzar recálculo desde el controller.
     */
    public void calcularParaMesActual() {
        YearMonth mesActual = YearMonth.now();
        int anio = mesActual.getYear();
        int mes = mesActual.getMonthValue();

        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        // Obtener tarifa activa
        Optional<Tarifa> tarifaOpt = tarifaRepository.findByActivaTrue();
        BigDecimal precioPorKwh = tarifaOpt
                .map(Tarifa::getPrecioPorKwh)
                .orElse(BigDecimal.valueOf(0.85));

        List<Ambiente> ambientes = ambienteRepository.findAll().stream()
                .filter(a -> a.getEstado() == AmbienteEstado.ACTIVO)
                .toList();

        int actualizados = 0;

        for (Ambiente ambiente : ambientes) {
            // Obtener todos los sensores del ambiente
            List<Sensor> sensores = sensorRepository.findByAmbienteId(ambiente.getId());

            // Sumar lecturas de todos los sensores del ambiente en el mes
            BigDecimal totalKwh = BigDecimal.ZERO;
            for (Sensor sensor : sensores) {
                List<Lectura> lecturas = lecturaRepository
                        .findBySensorIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                                sensor.getId(), inicioMes, finMes);

                BigDecimal sumaKwh = lecturas.stream()
                        .map(Lectura::getValorKwh)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalKwh = totalKwh.add(sumaKwh);
            }

            // Calcular costo
            BigDecimal costoEstimado = totalKwh.multiply(precioPorKwh)
                    .setScale(2, RoundingMode.HALF_UP);

            // Buscar o crear resumen
            Optional<ConsumoResumen> existente = consumoResumenRepository
                    .findByAmbienteIdAndPeriodoAnioAndPeriodoMes(ambiente.getId(), anio, mes);

            ConsumoResumen resumen;
            if (existente.isPresent()) {
                resumen = existente.get();
                resumen.setTotalKwh(totalKwh);
                resumen.setCostoEstimado(costoEstimado);
                resumen.setFechaCalculo(LocalDateTime.now());
                if (tarifaOpt.isPresent()) {
                    resumen.setTarifa(tarifaOpt.get());
                }
            } else {
                resumen = ConsumoResumen.builder()
                        .ambiente(ambiente)
                        .tarifa(tarifaOpt.orElse(null))
                        .periodoAnio(anio)
                        .periodoMes(mes)
                        .totalKwh(totalKwh)
                        .costoEstimado(costoEstimado)
                        .build();
            }

            consumoResumenRepository.save(resumen);
            actualizados++;
        }

        log.info("Consumo recalculado: {} ambientes actualizados para {}/{}", actualizados, mes, anio);
    }

    /**
     * Obtener todos los resúmenes de consumo.
     */
    public List<ConsumoResumen> listarTodos() {
        return consumoResumenRepository.findAll();
    }

    /**
     * Obtener resúmenes del mes actual.
     */
    public List<ConsumoResumen> listarMesActual() {
        YearMonth mesActual = YearMonth.now();
        return consumoResumenRepository.findByPeriodoAnioAndPeriodoMes(
                mesActual.getYear(), mesActual.getMonthValue());
    }

    /**
     * Obtener resúmenes por ambiente.
     */
    public List<ConsumoResumen> listarPorAmbiente(Long ambienteId) {
        return consumoResumenRepository.findByAmbienteId(ambienteId);
    }
}

package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.LecturaRequest;
import com.voltroom.voltroombackend.entity.Lectura;
import com.voltroom.voltroombackend.entity.Sensor;
import com.voltroom.voltroombackend.enums.LecturaOrigen;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.voltroom.voltroombackend.repository.LecturaRepository;
import com.voltroom.voltroombackend.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturaService {

    private final LecturaRepository lecturaRepository;
    private final SensorRepository sensorRepository;

    public List<Lectura> listar() {
        return lecturaRepository.findAll();
    }

    public List<Lectura> listarPorSensor(Long sensorId) {
        return lecturaRepository.findBySensorIdOrderByFechaHoraDesc(sensorId);
    }

    public Lectura obtener(Long id) {
        return lecturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lectura no encontrada"));
    }

    public Lectura crear(LecturaRequest request) {
        Sensor sensor = sensorRepository.findById(request.getSensorId())
                .orElseThrow(() -> new RuntimeException("Sensor no encontrado"));

        Lectura lectura = Lectura.builder()
                .sensor(sensor)
                .fechaHora(request.getFechaHora())
                .valorKwh(BigDecimal.valueOf(request.getValorKwh()))
                .origenLectura(LecturaOrigen.valueOf(request.getOrigenLectura()))
                .observacion(request.getObservacion())
                .build();

        return lecturaRepository.save(lectura);
    }

    public double calcularConsumo(Long sensorId, LocalDateTime inicio, LocalDateTime fin) {
        List<Lectura> lecturas = lecturaRepository
                .findBySensorIdAndFechaHoraBetweenOrderByFechaHoraAsc(sensorId, inicio, fin);

        return lecturas.stream()
                .mapToDouble(lectura -> lectura.getValorKwh().doubleValue())
                .sum();
    }

    public double calcularCosto(Long sensorId, LocalDateTime inicio, LocalDateTime fin, double tarifaPorKwh) {
        double consumo = calcularConsumo(sensorId, inicio, fin);
        return consumo * tarifaPorKwh;
    }

    public void eliminar(Long id) {
        lecturaRepository.deleteById(id);
    }

    /**
     * Agrupa las lecturas de un sensor por su origen utilizando Google Guava Multimaps.
     * Demuestra manipulación eficiente de colecciones en memoria.
     *
     * @param sensorId Identificador del sensor
     * @return Multimap con la agrupación de lecturas por origen
     */
    public Multimap<LecturaOrigen, Lectura> agruparLecturasPorOrigen(Long sensorId) {
        List<Lectura> lecturas = listarPorSensor(sensorId);
        return Multimaps.index(lecturas, Lectura::getOrigenLectura);
    }

    /**
     * Filtra lecturas por origen de manera eficiente en memoria usando Google Guava Iterables.
     *
     * @param sensorId Identificador del sensor
     * @param origen   Origen de la lectura
     * @return Lista filtrada de lecturas
     */
    public List<Lectura> filtrarPorOrigen(Long sensorId, LecturaOrigen origen) {
        List<Lectura> lecturas = listarPorSensor(sensorId);
        return Lists.newArrayList(Iterables.filter(lecturas, lectura -> lectura.getOrigenLectura() == origen));
    }
}

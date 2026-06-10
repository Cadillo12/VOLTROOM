package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.SensorDatosRequest;
import com.voltroom.voltroombackend.entity.Sensor;
import com.voltroom.voltroombackend.enums.SensorEstado;
import com.voltroom.voltroombackend.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simula sensores PZEM-004T / SDM120 enviando datos como haría un ESP32.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SensorSimuladorService implements CommandLineRunner {

    private final SensorRepository sensorRepository;
    private final TelemetriaService telemetriaService;
    private final SimpMessagingTemplate messagingTemplate;

    // Almacena el kWh acumulado por sensor
    private final ConcurrentHashMap<Long, BigDecimal> contadoresKwh = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        log.info("Simulador: Ejecutando simulación inicial de arranque...");
        generarLecturasAutomaticas();
    }

    /**
     * Genera lecturas simuladas cada 3 segundos para una sensación de tiempo real.
     */
    @Scheduled(fixedRate = 3000, initialDelay = 10000)
    public void generarLecturasAutomaticas() {
        try {
            // Buscamos todos los sensores. Si el filtro de estado falla, probamos con todos.
            List<Sensor> todos = sensorRepository.findAll();
            
            List<Sensor> sensores = todos.stream()
                    .filter(s -> s.getEstado() == SensorEstado.OPERATIVO || s.getEstado() == SensorEstado.FALLA)
                    .toList();

            if (sensores.isEmpty() && !todos.isEmpty()) {
                log.warn("Simulador: No hay sensores OPERATIVO/FALLA, pero hay {} sensores en BD. Simulando todos por seguridad.", todos.size());
                sensores = todos;
            } else if (todos.isEmpty()) {
                log.error("Simulador: ¡LA BASE DE DATOS DE SENSORES ESTÁ VACÍA!");
                return;
            }

            log.info("Simulador: Iniciando ciclo para {} sensores", sensores.size());

            for (Sensor sensor : sensores) {
                // 1. Simular Voltaje: 220V ± 5V
                double voltaje = 218.0 + ThreadLocalRandom.current().nextDouble(0, 4.0);

                // 2. Simular Potencia (W)
                double potenciaW = generarPotenciaBase(sensor);
                potenciaW = potenciaW * (0.95 + ThreadLocalRandom.current().nextDouble(0, 0.1));

                // 3. Simular Amperaje: I = P / V
                double amperaje = potenciaW / voltaje;

                // 4. Calcular incremento de kWh (3 seg)
                double incremento = (potenciaW / 1000.0) * (3.0 / 3600.0);

                // 5. Actualizar acumulado
                BigDecimal actual = contadoresKwh.getOrDefault(sensor.getId(), 
                    BigDecimal.valueOf(12.45)); // Valor base
                
                BigDecimal nuevoAcumulado = actual.add(BigDecimal.valueOf(incremento))
                        .setScale(6, RoundingMode.HALF_UP);
                contadoresKwh.put(sensor.getId(), nuevoAcumulado);

                // 6. Preparar Request
                SensorDatosRequest request = new SensorDatosRequest();
                request.setSensorId(sensor.getId());
                request.setKwhActual(nuevoAcumulado);
                request.setVoltaje(BigDecimal.valueOf(voltaje).setScale(1, RoundingMode.HALF_UP));
                request.setAmperaje(BigDecimal.valueOf(amperaje).setScale(2, RoundingMode.HALF_UP));
                request.setPotenciaW(BigDecimal.valueOf(potenciaW).setScale(1, RoundingMode.HALF_UP));

                // 7. Procesar
                telemetriaService.procesarDatosSensor(request);
            }
            
            // Broadcast a los clientes conectados vía WebSocket
            try {
                messagingTemplate.convertAndSend("/topic/telemetria", telemetriaService.obtenerEstadoTiempoReal());
            } catch (Exception e) {
                log.error("Simulador: Error al enviar datos por WebSocket", e);
            }
            
            log.info("Simulador: ✓ Ciclo completado correctamente.");
            
        } catch (Exception e) {
            log.error("Simulador: ERROR en ciclo: {}", e.getMessage(), e);
        }
    }

    private double generarPotenciaBase(Sensor sensor) {
        String cod = (sensor.getCodigo() != null ? sensor.getCodigo() : "").toUpperCase();
        if (cod.contains("COC")) return 1100.0;
        if (cod.contains("SRV")) return 1800.0;
        if (cod.contains("DORM")) return 250.0;
        if (cod.contains("LAB")) return 1400.0;
        return 450.0;
    }
}


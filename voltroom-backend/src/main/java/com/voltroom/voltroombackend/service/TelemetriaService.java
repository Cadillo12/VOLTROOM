package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.SensorDatosRequest;
import com.voltroom.voltroombackend.dto.SensorEstadoResponse;
import com.voltroom.voltroombackend.entity.*;
import com.voltroom.voltroombackend.enums.*;
import com.voltroom.voltroombackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de telemetría que procesa datos de sensores ESP32.
 *
 * Flujo:
 * 1. ESP32 envía datos (kWh acumulado, voltaje, amperaje)
 * 2. Se calcula el delta de consumo respecto a la última lectura
 * 3. Se verifica voltaje y potencia para generar alertas
 * 4. Se guarda la lectura y se actualiza el resumen de consumo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetriaService {

    private final SensorRepository sensorRepository;
    private final LecturaRepository lecturaRepository;
    private final AlertaRepository alertaRepository;
    private final TarifaRepository tarifaRepository;
    private final ConsumoResumenRepository consumoResumenRepository;
    private final AmbienteRepository ambienteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Umbrales de voltaje (estándar Perú: 220V ± 10%)
    private static final BigDecimal VOLTAJE_MIN = BigDecimal.valueOf(180);
    private static final BigDecimal VOLTAJE_MAX = BigDecimal.valueOf(240);
    private static final BigDecimal POTENCIA_MINIMA = BigDecimal.valueOf(0.5);

    /**
     * Procesa datos recibidos desde un sensor ESP32.
     * Calcula delta de kWh, genera alertas si es necesario.
     */
    public Lectura procesarDatosSensor(SensorDatosRequest request) {
        Sensor sensor = sensorRepository.findById(request.getSensorId())
                .orElseThrow(() -> new RuntimeException("Sensor no encontrado: " + request.getSensorId()));

        // 1. Obtener última lectura para calcular delta
        List<Lectura> ultimasLecturas = lecturaRepository
                .findBySensorIdOrderByFechaHoraDesc(sensor.getId());

        BigDecimal deltaKwh;
        if (!ultimasLecturas.isEmpty()) {
            Lectura ultima = ultimasLecturas.get(0);
            BigDecimal lecturaAnterior = obtenerKwhAcumulado(ultima);
            // Delta = Lectura actual - Lectura anterior (acumuladas)
            deltaKwh = request.getKwhActual().subtract(lecturaAnterior);
            if (deltaKwh.compareTo(BigDecimal.ZERO) < 0) {
                // Si es negativo, el medidor se reinició; usar el valor actual como delta
                deltaKwh = request.getKwhActual();
            }
        } else {
            // Primera lectura: usar valor directamente
            deltaKwh = request.getKwhActual();
        }

        // 2. Guardar lectura con el delta calculado
        String observacion = String.format(java.util.Locale.US, "V:%.1f A:%.2f W:%.1f kWh_acum:%.3f",
                request.getVoltaje() != null ? request.getVoltaje().doubleValue() : 0,
                request.getAmperaje() != null ? request.getAmperaje().doubleValue() : 0,
                request.getPotenciaW() != null ? request.getPotenciaW().doubleValue() : 0,
                request.getKwhActual().doubleValue());

        Lectura lectura = Lectura.builder()
                .sensor(sensor)
                .fechaHora(LocalDateTime.now())
                .valorKwh(deltaKwh.setScale(3, RoundingMode.HALF_UP))
                .origenLectura(LecturaOrigen.AUTOMATICA)
                .observacion(observacion)
                .build();

        lecturaRepository.save(lectura);
        log.info("Lectura recibida: Sensor={}, Delta={} kWh, Voltaje={}", 
                sensor.getCodigo(), deltaKwh, request.getVoltaje());

        // 3. Verificar alertas
        verificarAlertas(sensor, request);

        // 4. Actualizar resumen de consumo del mes
        actualizarConsumoMensual(sensor);

        return lectura;
    }

    private BigDecimal obtenerKwhAcumulado(Lectura lectura) {
        String obs = lectura.getObservacion();
        if (obs != null && obs.contains("kWh_acum:")) {
            try {
                String[] partes = obs.split(" ");
                for (String parte : partes) {
                    if (parte.startsWith("kWh_acum:")) {
                        return new BigDecimal(parte.substring(9).replace(',', '.'));
                    }
                }
            } catch (Exception ignored) {}
        }
        return lectura.getValorKwh();
    }

    /**
     * Verifica condiciones de alerta: voltaje fuera de rango, 0W con sensor activo.
     */
    private void verificarAlertas(Sensor sensor, SensorDatosRequest request) {
        Ambiente ambiente = sensor.getAmbiente();

        // Alerta: Voltaje fuera de rango
        if (request.getVoltaje() != null) {
            if (request.getVoltaje().compareTo(VOLTAJE_MIN) < 0 || 
                request.getVoltaje().compareTo(VOLTAJE_MAX) > 0) {
                
                crearAlertaAutomatica(
                        ambiente, sensor,
                        AlertaTipo.FALLA_SENSOR,
                        AlertaNivel.CRITICO,
                        "Voltaje fuera de rango en " + ambiente.getNombre(),
                        String.format("Voltaje detectado: %.1fV (rango aceptable: %.0f-%.0fV)",
                                request.getVoltaje().doubleValue(),
                                VOLTAJE_MIN.doubleValue(), VOLTAJE_MAX.doubleValue()),
                        request.getVoltaje(),
                        VOLTAJE_MAX
                );
            }
        }

        // Alerta: Potencia 0W con sensor operativo (posible fallo de suministro)
        if (request.getPotenciaW() != null &&
            request.getPotenciaW().compareTo(POTENCIA_MINIMA) < 0 &&
            sensor.getEstado() == SensorEstado.OPERATIVO) {
            
            crearAlertaAutomatica(
                    ambiente, sensor,
                    AlertaTipo.SIN_LECTURA,
                    AlertaNivel.ALTO,
                    "Sin consumo detectado en " + ambiente.getNombre(),
                    String.format("Potencia: %.1fW — Posible fallo de suministro o habitación sin uso.",
                            request.getPotenciaW().doubleValue()),
                    request.getPotenciaW(),
                    POTENCIA_MINIMA
            );
        }

        // Alerta: Sobreconsumo (potencia > 3000W, típico de sobrecarga)
        if (request.getPotenciaW() != null && request.getPotenciaW().compareTo(BigDecimal.valueOf(3000)) > 0) {
            crearAlertaAutomatica(
                    ambiente, sensor,
                    AlertaTipo.SOBRECONSUMO,
                    AlertaNivel.ALTO,
                    "Sobreconsumo detectado en " + ambiente.getNombre(),
                    String.format("Potencia: %.1fW — Posible sobrecarga eléctrica.",
                            request.getPotenciaW().doubleValue()),
                    request.getPotenciaW(),
                    BigDecimal.valueOf(3000)
            );
        }
    }

    private void crearAlertaAutomatica(Ambiente ambiente, Sensor sensor,
                                        AlertaTipo tipo, AlertaNivel nivel,
                                        String titulo, String mensaje,
                                        BigDecimal valorDetectado, BigDecimal umbral) {
        Alerta alerta = Alerta.builder()
                .ambiente(ambiente)
                .sensor(sensor)
                .tipoAlerta(tipo)
                .nivel(nivel)
                .titulo(titulo)
                .mensaje(mensaje)
                .valorDetectado(valorDetectado)
                .umbralReferencia(umbral)
                .atendida(false)
                .build();

        alertaRepository.save(alerta);
        
        // Cambiar automáticamente el estado del sensor a FALLA si estaba OPERATIVO
        if (sensor.getEstado() == SensorEstado.OPERATIVO) {
            sensor.setEstado(SensorEstado.FALLA);
            sensorRepository.save(sensor);
        }

        log.warn("ALERTA generada: [{}] {} - {}", nivel, titulo, mensaje);
        
        // Broadcast a los clientes conectados vía WebSocket
        try {
            messagingTemplate.convertAndSend("/topic/alertas", alerta);
        } catch (Exception e) {
            log.error("Error al enviar alerta por WebSocket", e);
        }
    }

    /**
     * Actualiza el resumen de consumo mensual del ambiente del sensor.
     */
    private void actualizarConsumoMensual(Sensor sensor) {
        Ambiente ambiente = sensor.getAmbiente();
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        // Sumar todas las lecturas del mes para todos los sensores del ambiente
        List<Sensor> sensoresAmbiente = sensorRepository.findByAmbienteId(ambiente.getId());
        BigDecimal totalKwh = BigDecimal.ZERO;

        for (Sensor s : sensoresAmbiente) {
            List<Lectura> lecturas = lecturaRepository
                    .findBySensorIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                            s.getId(), inicioMes, finMes);
            BigDecimal suma = lecturas.stream()
                    .map(Lectura::getValorKwh)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalKwh = totalKwh.add(suma);
        }

        // Tarifa activa
        BigDecimal precioPorKwh = tarifaRepository.findByActivaTrue()
                .map(Tarifa::getPrecioPorKwh)
                .orElse(BigDecimal.valueOf(0.85));

        BigDecimal costoEstimado = totalKwh.multiply(precioPorKwh)
                .setScale(2, RoundingMode.HALF_UP);

        // Actualizar o crear resumen
        Optional<ConsumoResumen> existente = consumoResumenRepository
                .findByAmbienteIdAndPeriodoAnioAndPeriodoMes(
                        ambiente.getId(), mesActual.getYear(), mesActual.getMonthValue());

        ConsumoResumen resumen;
        if (existente.isPresent()) {
            resumen = existente.get();
            resumen.setTotalKwh(totalKwh);
            resumen.setCostoEstimado(costoEstimado);
            resumen.setFechaCalculo(LocalDateTime.now());
        } else {
            resumen = ConsumoResumen.builder()
                    .ambiente(ambiente)
                    .tarifa(tarifaRepository.findByActivaTrue().orElse(null))
                    .periodoAnio(mesActual.getYear())
                    .periodoMes(mesActual.getMonthValue())
                    .totalKwh(totalKwh)
                    .costoEstimado(costoEstimado)
                    .build();
        }

        consumoResumenRepository.save(resumen);
    }

    /**
     * Obtiene el estado en tiempo real de todos los sensores.
     * Usado por el frontend para mostrar contadores que se actualizan cada 5 seg.
     */
    public List<SensorEstadoResponse> obtenerEstadoTiempoReal() {
        List<Sensor> sensores = sensorRepository.findAll();
        List<SensorEstadoResponse> estados = new ArrayList<>();

        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        BigDecimal precioPorKwh = tarifaRepository.findByActivaTrue()
                .map(Tarifa::getPrecioPorKwh)
                .orElse(BigDecimal.valueOf(0.85));

        for (Sensor sensor : sensores) {
            // Última lectura
            List<Lectura> ultimas = lecturaRepository
                    .findBySensorIdOrderByFechaHoraDesc(sensor.getId());

            // Consumo del mes
            List<Lectura> lecturasMes = lecturaRepository
                    .findBySensorIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                            sensor.getId(), inicioMes, finMes);
            BigDecimal consumoMes = lecturasMes.stream()
                    .map(Lectura::getValorKwh)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Parsear voltaje/amperaje de la última observación
            BigDecimal voltaje = BigDecimal.ZERO;
            BigDecimal amperaje = BigDecimal.ZERO;
            BigDecimal potencia = BigDecimal.ZERO;
            BigDecimal kwhAcumulado = BigDecimal.ZERO;

            if (!ultimas.isEmpty()) {
                Lectura ultima = ultimas.get(0);
                kwhAcumulado = ultima.getValorKwh();
                String obs = ultima.getObservacion();
                if (obs != null && obs.startsWith("V:")) {
                    try {
                        String[] partes = obs.split(" ");
                        for (String parte : partes) {
                            String val = parte.substring(parte.indexOf(':') + 1).replace(',', '.');
                            if (parte.startsWith("V:")) voltaje = new BigDecimal(val);
                            if (parte.startsWith("A:")) amperaje = new BigDecimal(val);
                            if (parte.startsWith("W:")) potencia = new BigDecimal(val);
                            if (parte.startsWith("kWh_acum:")) kwhAcumulado = new BigDecimal(val);
                        }
                    } catch (Exception ignored) {}
                }
            }

            String estadoVisual = sensor.getEstado().name();
            
            // Lógica visual para la tabla de Monitoreo:
            // Si el sensor está en FALLA, evaluamos el voltaje para determinar si es CRITICO (rojo) o se queda en FALLA (amarillo)
            if (sensor.getEstado() == SensorEstado.FALLA) {
                if (voltaje.compareTo(VOLTAJE_MIN) < 0 || voltaje.compareTo(VOLTAJE_MAX) > 0) {
                    estadoVisual = "CRITICO";
                }
            }

            estados.add(SensorEstadoResponse.builder()
                    .sensorId(sensor.getId())
                    .codigoSensor(sensor.getCodigo())
                    .ambienteNombre(sensor.getAmbiente().getNombre())
                    .inmuebleNombre(sensor.getAmbiente().getInmueble() != null
                            ? sensor.getAmbiente().getInmueble().getNombre() : "-")
                    .kwhAcumulado(kwhAcumulado)
                    .kwhConsumoMes(consumoMes.setScale(3, RoundingMode.HALF_UP))
                    .voltaje(voltaje)
                    .amperaje(amperaje)
                    .potenciaW(potencia)
                    .costoEstimadoMes(consumoMes.multiply(precioPorKwh).setScale(2, RoundingMode.HALF_UP))
                    .estado(estadoVisual)
                    .ultimaLectura(!ultimas.isEmpty() ? ultimas.get(0).getFechaHora() : null)
                    .build());
        }

        return estados;
    }

    /**
     * Método para forzar una lectura en todos los sensores (útil para pruebas)
     */
    public void forzarSimulacionManual() {
        List<Sensor> sensores = sensorRepository.findAll();
        log.info("Forzando simulación manual para {} sensores", sensores.size());
        for (Sensor s : sensores) {
            SensorDatosRequest req = new SensorDatosRequest();
            req.setSensorId(s.getId());
            req.setKwhActual(BigDecimal.valueOf(10.5));
            req.setVoltaje(BigDecimal.valueOf(220.5));
            req.setAmperaje(BigDecimal.valueOf(2.5));
            req.setPotenciaW(BigDecimal.valueOf(550.0));
            procesarDatosSensor(req);
        }
    }
}

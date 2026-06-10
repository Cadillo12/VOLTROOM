package com.voltroom.voltroombackend.service;

import com.google.common.collect.Multimap;
import com.voltroom.voltroombackend.entity.Lectura;
import com.voltroom.voltroombackend.enums.LecturaOrigen;
import com.voltroom.voltroombackend.repository.LecturaRepository;
import com.voltroom.voltroombackend.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LecturaServiceTest {

    @Mock
    private LecturaRepository lecturaRepository;

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private LecturaService lecturaService;

    private List<Lectura> lecturasMock;

    @BeforeEach
    void setUp() {
        Lectura l1 = Lectura.builder()
                .id(1L)
                .valorKwh(BigDecimal.valueOf(10.5))
                .origenLectura(LecturaOrigen.AUTOMATICA)
                .build();
        Lectura l2 = Lectura.builder()
                .id(2L)
                .valorKwh(BigDecimal.valueOf(5.0))
                .origenLectura(LecturaOrigen.MANUAL)
                .build();
        Lectura l3 = Lectura.builder()
                .id(3L)
                .valorKwh(BigDecimal.valueOf(8.5))
                .origenLectura(LecturaOrigen.AUTOMATICA)
                .build();
        lecturasMock = Arrays.asList(l1, l2, l3);
    }

    @Test
    void testAgruparLecturasPorOrigen() {
        Long sensorId = 1L;
        when(lecturaRepository.findBySensorIdOrderByFechaHoraDesc(sensorId)).thenReturn(lecturasMock);

        Multimap<LecturaOrigen, Lectura> agrupado = lecturaService.agruparLecturasPorOrigen(sensorId);

        assertEquals(2, agrupado.get(LecturaOrigen.AUTOMATICA).size());
        assertEquals(1, agrupado.get(LecturaOrigen.MANUAL).size());
    }

    @Test
    void testFiltrarPorOrigen() {
        Long sensorId = 1L;
        when(lecturaRepository.findBySensorIdOrderByFechaHoraDesc(sensorId)).thenReturn(lecturasMock);

        List<Lectura> filtrado = lecturaService.filtrarPorOrigen(sensorId, LecturaOrigen.MANUAL);

        assertEquals(1, filtrado.size());
        assertEquals(LecturaOrigen.MANUAL, filtrado.get(0).getOrigenLectura());
    }
}

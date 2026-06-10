package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.InmuebleRequest;
import com.voltroom.voltroombackend.entity.Inmueble;
import com.voltroom.voltroombackend.repository.InmuebleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InmuebleServiceTest {

    @Mock
    private InmuebleRepository inmuebleRepository;

    @InjectMocks
    private InmuebleService inmuebleService;

    private Inmueble inmuebleMock;

    @BeforeEach
    void setUp() {
        inmuebleMock = Inmueble.builder()
                .id(1L)
                .nombre("Edificio Central")
                .direccion("Av. Lima 123")
                .build();
    }

    @Test
    void crearInmueble_Exito() {
        InmuebleRequest request = new InmuebleRequest();
        request.setNombre("Edificio Central");
        request.setDireccion("Av. Lima 123");

        when(inmuebleRepository.save(any(Inmueble.class))).thenReturn(inmuebleMock);

        Inmueble resultado = inmuebleService.crear(request);

        assertNotNull(resultado);
        assertEquals("Edificio Central", resultado.getNombre());
        verify(inmuebleRepository, times(1)).save(any(Inmueble.class));
    }

    @Test
    void obtenerInmueble_NoEncontrado_LanzaExcepcion() {
        when(inmuebleRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inmuebleService.obtener(99L);
        });

        assertTrue(exception.getMessage().contains("no encontrado"));
    }
}

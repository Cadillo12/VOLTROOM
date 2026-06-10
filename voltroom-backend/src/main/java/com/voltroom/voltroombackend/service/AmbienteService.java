package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.AmbienteRequest;
import com.voltroom.voltroombackend.entity.Ambiente;
import com.voltroom.voltroombackend.entity.Inmueble;
import com.voltroom.voltroombackend.enums.AmbienteEstado;
import com.voltroom.voltroombackend.repository.AmbienteRepository;
import com.voltroom.voltroombackend.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;
    private final InmuebleRepository inmuebleRepository;

    public List<Ambiente> listar() {
        return ambienteRepository.findAll();
    }

    public List<Ambiente> listarPorInmueble(Long inmuebleId) {
        return ambienteRepository.findByInmuebleId(inmuebleId);
    }

    public Ambiente obtener(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambiente no encontrado"));
    }

    public Ambiente crear(AmbienteRequest request) {
        if (request.getInmuebleId() == null) {
            throw new RuntimeException("El ID del inmueble es obligatorio");
        }

        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RuntimeException("Inmueble no encontrado con ID: " + request.getInmuebleId()));

        AmbienteEstado estado;
        try {
            estado = (request.getEstado() != null && !request.getEstado().isBlank()) 
                     ? AmbienteEstado.valueOf(request.getEstado().toUpperCase()) 
                     : AmbienteEstado.ACTIVO;
        } catch (IllegalArgumentException e) {
            estado = AmbienteEstado.ACTIVO;
        }

        Ambiente ambiente = Ambiente.builder()
                .inmueble(inmueble)
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .piso(request.getPiso())
                .estado(estado)
                .descripcion(request.getDescripcion())
                .build();

        return ambienteRepository.save(ambiente);
    }

    public Ambiente actualizar(Long id, AmbienteRequest request) {
        Ambiente ambiente = obtener(id);

        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RuntimeException("Inmueble no encontrado con ID: " + request.getInmuebleId()));

        AmbienteEstado estado;
        try {
            estado = (request.getEstado() != null && !request.getEstado().isBlank()) 
                     ? AmbienteEstado.valueOf(request.getEstado().toUpperCase()) 
                     : AmbienteEstado.ACTIVO;
        } catch (IllegalArgumentException e) {
            estado = AmbienteEstado.ACTIVO;
        }

        ambiente.setInmueble(inmueble);
        ambiente.setNombre(request.getNombre());
        ambiente.setTipo(request.getTipo());
        ambiente.setPiso(request.getPiso());
        ambiente.setEstado(estado);
        ambiente.setDescripcion(request.getDescripcion());

        return ambienteRepository.save(ambiente);
    }

    public void eliminar(Long id) {
        ambienteRepository.deleteById(id);
    }
}

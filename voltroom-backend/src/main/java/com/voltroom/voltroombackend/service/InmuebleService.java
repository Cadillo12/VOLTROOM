package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.InmuebleRequest;
import com.voltroom.voltroombackend.entity.Inmueble;
import com.voltroom.voltroombackend.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InmuebleService {

    private final InmuebleRepository inmuebleRepository;

    public List<Inmueble> listar() {
        return inmuebleRepository.findAll();
    }

    public Inmueble obtener(Long id) {
        return inmuebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inmueble no encontrado"));
    }

    public Inmueble crear(InmuebleRequest request) {
        Inmueble inmueble = Inmueble.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
        return inmuebleRepository.save(inmueble);
    }

    public Inmueble actualizar(Long id, InmuebleRequest request) {
        Inmueble inmueble = obtener(id);
        inmueble.setNombre(request.getNombre());
        inmueble.setDireccion(request.getDireccion());
        inmueble.setDescripcion(request.getDescripcion());
        inmueble.setActivo(request.getActivo() != null ? request.getActivo() : inmueble.getActivo());
        return inmuebleRepository.save(inmueble);
    }

    public void eliminar(Long id) {
        inmuebleRepository.deleteById(id);
    }
}

package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.TarifaRequest;
import com.voltroom.voltroombackend.entity.Tarifa;
import com.voltroom.voltroombackend.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public List<Tarifa> listar() {
        return tarifaRepository.findAll();
    }

    public Tarifa obtener(Long id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
    }

    public Tarifa obtenerActiva() {
        return tarifaRepository.findByActivaTrue()
                .orElseThrow(() -> new RuntimeException("No existe tarifa activa"));
    }

    public Tarifa crear(TarifaRequest request) {
        if (Boolean.TRUE.equals(request.getActiva())) {
            desactivarTarifasActivas();
        }

        Tarifa tarifa = Tarifa.builder()
                .nombre(request.getNombre())
                .precioPorKwh(request.getPrecioPorKwh())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activa(request.getActiva() != null ? request.getActiva() : true)
                .descripcion(request.getDescripcion())
                .build();

        return tarifaRepository.save(tarifa);
    }

    public Tarifa actualizar(Long id, TarifaRequest request) {
        Tarifa tarifa = obtener(id);

        if (Boolean.TRUE.equals(request.getActiva())) {
            desactivarTarifasActivas();
        }

        tarifa.setNombre(request.getNombre());
        tarifa.setPrecioPorKwh(request.getPrecioPorKwh());
        tarifa.setFechaInicio(request.getFechaInicio());
        tarifa.setFechaFin(request.getFechaFin());
        tarifa.setActiva(request.getActiva() != null ? request.getActiva() : tarifa.getActiva());
        tarifa.setDescripcion(request.getDescripcion());

        return tarifaRepository.save(tarifa);
    }

    public void eliminar(Long id) {
        tarifaRepository.deleteById(id);
    }

    private void desactivarTarifasActivas() {
        List<Tarifa> tarifas = tarifaRepository.findAll();
        for (Tarifa t : tarifas) {
            if (Boolean.TRUE.equals(t.getActiva())) {
                t.setActiva(false);
                tarifaRepository.save(t);
            }
        }
    }
}
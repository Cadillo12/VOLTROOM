package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Incidencia;
import com.voltroom.voltroombackend.enums.IncidenciaEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByEstadoIn(List<IncidenciaEstado> estados);
}
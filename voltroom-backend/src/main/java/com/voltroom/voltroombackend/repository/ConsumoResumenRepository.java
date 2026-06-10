package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.ConsumoResumen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumoResumenRepository extends JpaRepository<ConsumoResumen, Long> {
    Optional<ConsumoResumen> findByAmbienteIdAndPeriodoAnioAndPeriodoMes(Long ambienteId, Integer anio, Integer mes);
    List<ConsumoResumen> findByPeriodoAnioAndPeriodoMes(Integer anio, Integer mes);
    List<ConsumoResumen> findByAmbienteId(Long ambienteId);
}
package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LecturaRepository extends JpaRepository<Lectura, Long> {
    List<Lectura> findBySensorIdOrderByFechaHoraDesc(Long sensorId);

    List<Lectura> findBySensorIdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long sensorId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}

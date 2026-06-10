package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByAmbienteId(Long ambienteId);
}

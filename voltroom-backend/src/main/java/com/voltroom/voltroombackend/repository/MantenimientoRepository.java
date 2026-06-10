package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Mantenimiento;
import com.voltroom.voltroombackend.enums.MantenimientoEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findByEstado(MantenimientoEstado estado);
}
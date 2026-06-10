package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
    List<Ambiente> findByInmuebleId(Long inmuebleId);
}

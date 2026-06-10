package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    Optional<Tarifa> findByActivaTrue();
}
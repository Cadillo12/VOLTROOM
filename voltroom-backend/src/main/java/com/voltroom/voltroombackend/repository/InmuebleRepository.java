package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InmuebleRepository extends JpaRepository<Inmueble, Long> {
}

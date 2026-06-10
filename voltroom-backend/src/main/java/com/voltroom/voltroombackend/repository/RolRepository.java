package com.voltroom.voltroombackend.repository;

import com.voltroom.voltroombackend.entity.Rol;
import com.voltroom.voltroombackend.enums.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(NombreRol nombre);
}
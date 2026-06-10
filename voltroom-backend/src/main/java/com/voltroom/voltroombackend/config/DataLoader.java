package com.voltroom.voltroombackend.config;

import com.voltroom.voltroombackend.entity.Rol;
import com.voltroom.voltroombackend.enums.NombreRol;
import com.voltroom.voltroombackend.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        for (NombreRol nombreRol : NombreRol.values()) {
            rolRepository.findByNombre(nombreRol)
                    .orElseGet(() -> rolRepository.save(Rol.builder().nombre(nombreRol).build()));
        }
    }
}

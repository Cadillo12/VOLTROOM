package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.AuthResponse;
import com.voltroom.voltroombackend.dto.LoginRequest;
import com.voltroom.voltroombackend.dto.RegisterRequest;
import com.voltroom.voltroombackend.entity.Rol;
import com.voltroom.voltroombackend.entity.Usuario;
import com.voltroom.voltroombackend.enums.NombreRol;
import com.voltroom.voltroombackend.repository.RolRepository;
import com.voltroom.voltroombackend.repository.UsuarioRepository;
import com.voltroom.voltroombackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();
        
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        NombreRol nombreRol = NombreRol.ROLE_OPERADOR;
        if (request.getRol() != null && !request.getRol().trim().isEmpty()) {
            try {
                nombreRol = NombreRol.valueOf(request.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Rol proporcionado no es válido");
            }
        }

        Rol rolUser = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado en la base de datos"));

        Usuario usuario = Usuario.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .activo(true)
                .roles(Set.of(rolUser))
                .build();

        usuarioRepository.save(usuario);
        return "Usuario registrado correctamente";
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase();
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(usuario.getEmail());

        List<String> roles = usuario.getRoles().stream()
                .map(r -> r.getNombre().name())
                .toList();

        return new AuthResponse(token, "Bearer", usuario.getEmail(), roles);
    }
}

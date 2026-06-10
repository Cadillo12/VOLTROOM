package com.voltroom.voltroombackend.security;

import com.voltroom.voltroombackend.entity.Usuario;
import com.voltroom.voltroombackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getNombre().name()))
                        .collect(Collectors.toList())
        );
    }
}

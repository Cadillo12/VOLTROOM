package com.voltroom.voltroombackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipo;
    private String email;
    private List<String> roles;
}

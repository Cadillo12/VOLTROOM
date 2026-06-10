package com.voltroom.voltroombackend.config;

// NOTA: La configuración de base de datos ahora se gestiona mediante
// application.properties y la autoconfiguración de Spring Boot.
// Se eliminó la configuración manual de DataSource/EntityManager
// para evitar conflictos de conexión.

import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    // Spring Boot auto-configura DataSource, EntityManagerFactory
    // y TransactionManager desde application.properties
}

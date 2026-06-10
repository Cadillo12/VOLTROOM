package com.voltroom.voltroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voltroom.voltroombackend.enums.IncidenciaEstado;
import com.voltroom.voltroombackend.enums.IncidenciaPrioridad;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ambiente_id")
    @JsonIgnoreProperties({"inmueble"})
    private Ambiente ambiente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_id")
    @JsonIgnoreProperties({"ambiente"})
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reportado_por")
    @JsonIgnoreProperties({"roles", "passwordHash"})
    private Usuario reportadoPor;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private IncidenciaPrioridad prioridad = IncidenciaPrioridad.MEDIA;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private IncidenciaEstado estado = IncidenciaEstado.ABIERTA;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "observacion_cierre", length = 255)
    private String observacionCierre;

    @PrePersist
    public void prePersist() {
        this.fechaReporte = LocalDateTime.now();
    }
}
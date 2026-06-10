package com.voltroom.voltroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voltroom.voltroombackend.enums.SensorEstado;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ambiente_id")
    @JsonIgnoreProperties({"inmueble"})
    private Ambiente ambiente;

    @Column(nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(name = "tipo_sensor", length = 80)
    private String tipoSensor;

    @Builder.Default
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida = "kWh";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private SensorEstado estado = SensorEstado.OPERATIVO;

    @Column(name = "umbral_maximo_kwh", precision = 12, scale = 2)
    private BigDecimal umbralMaximoKwh;

    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion;

    @Column(length = 255)
    private String observacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
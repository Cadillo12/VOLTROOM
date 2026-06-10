package com.voltroom.voltroombackend.entity;

import com.voltroom.voltroombackend.enums.LecturaOrigen;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "lectura",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_lectura_sensor_fecha", columnNames = {"sensor_id", "fecha_hora"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "valor_kwh", nullable = false, precision = 12, scale = 3)
    private BigDecimal valorKwh;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "origen_lectura", nullable = false)
    private LecturaOrigen origenLectura = LecturaOrigen.MANUAL;

    @Column(length = 255)
    private String observacion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}
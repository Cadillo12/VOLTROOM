package com.voltroom.voltroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voltroom.voltroombackend.enums.AlertaNivel;
import com.voltroom.voltroombackend.enums.AlertaTipo;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

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

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_alerta", nullable = false)
    private AlertaTipo tipoAlerta;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private AlertaNivel nivel = AlertaNivel.MEDIO;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 255)
    private String mensaje;

    @Column(name = "valor_detectado", precision = 12, scale = 3)
    private BigDecimal valorDetectado;

    @Column(name = "umbral_referencia", precision = 12, scale = 3)
    private BigDecimal umbralReferencia;

    @Builder.Default
    @Column(nullable = false)
    private Boolean atendida = false;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "fecha_atencion")
    private LocalDateTime fechaAtencion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "atendida_por")
    @JsonIgnoreProperties({"roles", "passwordHash"})
    private Usuario atendidaPor;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
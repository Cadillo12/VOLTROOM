package com.voltroom.voltroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "consumo_resumen",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_consumo_resumen_periodo", columnNames = {"ambiente_id", "periodo_anio", "periodo_mes"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumoResumen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ambiente_id")
    @JsonIgnoreProperties({"descripcion", "fechaCreacion", "fechaActualizacion"})
    private Ambiente ambiente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tarifa_id")
    @JsonIgnoreProperties({"descripcion", "fechaCreacion"})
    private Tarifa tarifa;

    @Column(name = "periodo_anio", nullable = false)
    private Integer periodoAnio;

    @Column(name = "periodo_mes", nullable = false)
    private Integer periodoMes;

    @Column(name = "total_kwh", nullable = false, precision = 12, scale = 3)
    private BigDecimal totalKwh = BigDecimal.ZERO;

    @Column(name = "costo_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEstimado = BigDecimal.ZERO;

    @Column(name = "fecha_calculo", nullable = false)
    private LocalDateTime fechaCalculo;

    @PrePersist
    public void prePersist() {
        this.fechaCalculo = LocalDateTime.now();
    }
}
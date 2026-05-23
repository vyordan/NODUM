package com.app.demoapp.model;

import com.app.demoapp.model.enums.EstadoPostulacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "postulaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajo_id", nullable = false)
    private Trabajo trabajo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Usuario trabajador;

    @Column(columnDefinition = "TEXT")
    private String mensajePresentacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPostulacion estado = EstadoPostulacion.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime fechaPostulacion = LocalDateTime.now();

    @OneToOne(mappedBy = "postulacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Contrato contrato;
}
package com.gymapp.ms_clases.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String disciplina;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(name = "entrenador_id", nullable = false)
    private Long entrenadorId;


    @Column(nullable = false)
    private boolean activa = true;
}
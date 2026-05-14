package com.gymapp.ms_clases.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseResponseDTO {
    private Long id;
    private String nombre;
    private String disciplina;
    private Integer capacidad;
    private Long entrenadorId;
    private boolean activa;
}
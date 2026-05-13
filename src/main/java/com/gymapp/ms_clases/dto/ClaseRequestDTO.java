package com.gymapp.ms_clases.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseRequestDTO {
    @NotBlank(message = "El nombre de la clase es obligatorio")
    private String nombre;

    @NotBlank(message = "La disciplina es obligatoria")
    private String disciplina;

    @NotNull(message = "La capacidad no puede estar vacía")
    @Min(value = 1, message = "La clase debe tener al menos 1 cupo")
    private Integer capacidad;

    @NotNull(message = "El ID del entrenador es obligatorio")
    private Long entrenadorId;
}
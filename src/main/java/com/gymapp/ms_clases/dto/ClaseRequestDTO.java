package com.gymapp.ms_clases.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto para la creación o actualización de una clase en el gimnasio")
public class ClaseRequestDTO {

    @NotBlank(message = "El nombre de la clase es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre descriptivo de la clase", example = "CrossFit Elite AM")
    private String nombre;

    @NotBlank(message = "La disciplina es obligatoria (Ej: Yoga, Crossfit, HIIT)")
    @Size(min = 3, max = 50, message = "La disciplina debe tener entre 3 y 50 caracteres")
    @Schema(description = "Categoría o disciplina impartida", example = "CrossFit")
    private String disciplina;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La clase debe tener capacidad para al menos 1 persona")
    @Max(value = 50, message = "La capacidad máxima por sala es de 50 personas")
    @Schema(description = "Aforo máximo permitido en la sala", example = "15")
    private Integer capacidad;

    @NotNull(message = "El ID del entrenador es obligatorio")
    @Schema(description = "ID del entrenador asignado a la clase (ms-entrenadores)", example = "1")
    private Long entrenadorId;
}
package com.gymapp.ms_clases.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto que representa la solicitud de un miembro para reservar un cupo en una clase")
public class ReservaRequestDTO {

    @NotNull(message = "El ID del miembro es obligatorio para la reserva")
    @Schema(description = "ID único del miembro que asiste (ms-miembros)", example = "120")
    private Long miembroId;

    @NotNull(message = "El ID de la clase es obligatorio")
    @Schema(description = "ID de la clase a reservar", example = "5")
    private Long claseId;
}
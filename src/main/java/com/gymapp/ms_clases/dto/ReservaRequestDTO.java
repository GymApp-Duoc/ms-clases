package com.gymapp.ms_clases.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El ID del miembro es obligatorio para la reserva")
    private Long miembroId;

    @NotNull(message = "El ID de la clase es obligatorio")
    private Long claseId;
}
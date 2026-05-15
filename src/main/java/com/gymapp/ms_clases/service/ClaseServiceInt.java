package com.gymapp.ms_clases.service;

import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ClaseServiceInt {

    List<ClaseResponseDTO> listarTodas();

    Optional<ClaseResponseDTO> obtenerPorId(Long id);

    List<ClaseResponseDTO> buscarPorEntrenador(Long entrenadorId);

    ClaseResponseDTO crear(ClaseRequestDTO dto);

    Optional<ClaseResponseDTO> actualizar(Long id, ClaseRequestDTO dto);

    Optional<ClaseResponseDTO> reducirCupo(Long id, Long miembroId);

    void eliminar(Long id);
}
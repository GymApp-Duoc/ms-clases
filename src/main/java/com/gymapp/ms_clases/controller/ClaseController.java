package com.gymapp.ms_clases.controller;

import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.dto.ReservaRequestDTO;
import com.gymapp.ms_clases.service.ClaseServiceInt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseServiceInt service;
    @GetMapping
    public ResponseEntity<List<ClaseResponseDTO>> obtenerTodas() {
        log.info("Petición REST recibida: Listar todas las clases");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST recibida: Obtener clase con ID {}", id);
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entrenador/{entrenadorId}")
    public ResponseEntity<List<ClaseResponseDTO>> listarPorEntrenador(@PathVariable Long entrenadorId) {
        log.info("Petición REST recibida: Listar clases para el entrenador ID {}", entrenadorId);
        return ResponseEntity.ok(service.buscarPorEntrenador(entrenadorId));
    }

    @PostMapping
    public ResponseEntity<ClaseResponseDTO> crear(@Valid @RequestBody ClaseRequestDTO dto) {
        log.info("Petición REST recibida: Crear nueva clase '{}'", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ClaseRequestDTO dto) {
        log.info("Petición REST recibida: Actualizar clase con ID {}", id);
        return service.actualizar(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/reservar")
    public ResponseEntity<ClaseResponseDTO> reservarCupo(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO dto) {
        log.info("Petición REST recibida: Miembro ID {} reservando cupo en clase ID {}", dto.getMiembroId(), id);
        return service.reducirCupo(id, dto.getMiembroId()).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición REST recibida: Eliminar clase con ID {}", id);
        if (service.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
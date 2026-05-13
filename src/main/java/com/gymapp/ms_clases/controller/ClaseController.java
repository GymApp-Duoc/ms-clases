package com.gymapp.ms_clases.controller;

import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.service.ClaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService service;

    @GetMapping
    public ResponseEntity<List<ClaseResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entrenador/{entrenadorId}")
    public ResponseEntity<List<ClaseResponseDTO>> listarPorEntrenador(@PathVariable Long entrenadorId) {
        return ResponseEntity.ok(service.buscarPorEntrenador(entrenadorId));
    }

    @PostMapping
    public ResponseEntity<ClaseResponseDTO> crear(@Valid @RequestBody ClaseRequestDTO dto) {
        return ResponseEntity.status(201).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ClaseRequestDTO dto) {
        return service.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/reducir-cupo")
    public ResponseEntity<ClaseResponseDTO> reducirCupo(@PathVariable Long id, @RequestParam Long miembroId) {
        return service.reducirCupo(id, miembroId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
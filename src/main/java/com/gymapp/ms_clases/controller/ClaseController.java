package com.gymapp.ms_clases.controller;

import com.gymapp.ms_clases.assembler.ClaseModelAssembler;
import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.dto.ReservaRequestDTO;
import com.gymapp.ms_clases.service.ClaseServiceInt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
@Tag(name = "Clases", description = "API para la gestión de catálogo de clases y reserva de cupos")
public class ClaseController {

    private final ClaseServiceInt service;
    private final ClaseModelAssembler assembler;

    @GetMapping
    @Operation(summary = "Listar todas las clases", description = "Retorna el catálogo completo de clases activas e inactivas con enlaces navegables")
    public ResponseEntity<CollectionModel<EntityModel<ClaseResponseDTO>>> obtenerTodas() {
        log.info("Petición REST recibida: Listar todas las clases");
        List<EntityModel<ClaseResponseDTO>> clases = service.listarTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(clases,
                linkTo(methodOn(ClaseController.class).obtenerTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener clase por ID", description = "Busca y retorna el detalle de una clase específica")
    public ResponseEntity<EntityModel<ClaseResponseDTO>> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST recibida: Obtener clase con ID {}", id);
        return service.obtenerPorId(id)
                .map(dto -> ResponseEntity.ok(assembler.toModel(dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entrenador/{entrenadorId}")
    @Operation(summary = "Listar clases por Entrenador", description = "Retorna las clases asignadas a un entrenador específico")
    public ResponseEntity<CollectionModel<EntityModel<ClaseResponseDTO>>> listarPorEntrenador(@PathVariable Long entrenadorId) {
        log.info("Petición REST recibida: Listar clases para el entrenador ID {}", entrenadorId);
        List<EntityModel<ClaseResponseDTO>> clases = service.buscarPorEntrenador(entrenadorId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(clases,
                linkTo(methodOn(ClaseController.class).listarPorEntrenador(entrenadorId)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Crear nueva clase", description = "Registra una nueva clase en el sistema validando que el entrenador exista")
    public ResponseEntity<EntityModel<ClaseResponseDTO>> crear(@Valid @RequestBody ClaseRequestDTO dto) {
        log.info("Petición REST recibida: Crear nueva clase '{}'", dto.getNombre());
        ClaseResponseDTO nuevaClase = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(nuevaClase));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar clase", description = "Actualiza los datos de una clase existente")
    public ResponseEntity<EntityModel<ClaseResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody ClaseRequestDTO dto) {
        log.info("Petición REST recibida: Actualizar clase con ID {}", id);
        return service.actualizar(id, dto)
                .map(actualizado -> ResponseEntity.ok(assembler.toModel(actualizado)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/reservar")
    @Operation(summary = "Reservar cupo en clase", description = "Reduce el aforo de una clase y gatilla eventos de gamificación y notificaciones")
    public ResponseEntity<EntityModel<ClaseResponseDTO>> reservarCupo(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO dto) {
        log.info("Petición REST recibida: Miembro ID {} reservando cupo en clase ID {}", dto.getMiembroId(), id);
        return service.reducirCupo(id, dto.getMiembroId())
                .map(reservado -> ResponseEntity.ok(assembler.toModel(reservado)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar clase", description = "Realiza un borrado lógico (desactiva) la clase por su ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición REST recibida: Eliminar clase con ID {}", id);
        if (service.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/reportes/disponibles")
    @Operation(summary = "Reporte 1: Clases Disponibles", description = "Lista todas las clases que están activas y aún tienen cupos")
    public ResponseEntity<CollectionModel<EntityModel<ClaseResponseDTO>>> reporteDisponibles() {
        List<EntityModel<ClaseResponseDTO>> clases = service.obtenerClasesDisponibles().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clases,
                linkTo(methodOn(ClaseController.class).reporteDisponibles()).withSelfRel()));
    }

    @GetMapping("/reportes/disciplina/{disciplina}")
    @Operation(summary = "Reporte 2: Clases por Disciplina", description = "Busca clases activas por nombre de disciplina (Ej: CrossFit)")
    public ResponseEntity<CollectionModel<EntityModel<ClaseResponseDTO>>> reportePorDisciplina(@PathVariable String disciplina) {
        List<EntityModel<ClaseResponseDTO>> clases = service.obtenerPorDisciplina(disciplina).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clases,
                linkTo(methodOn(ClaseController.class).reportePorDisciplina(disciplina)).withSelfRel()));
    }

    @GetMapping("/reportes/populares")
    @Operation(summary = "Reporte 3: Clases Populares", description = "Muestra las clases que están a punto de llenarse (1 a 5 cupos restantes)")
    public ResponseEntity<CollectionModel<EntityModel<ClaseResponseDTO>>> reporteCasiLlenas() {
        List<EntityModel<ClaseResponseDTO>> clases = service.obtenerClasesCasiLlenas().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clases,
                linkTo(methodOn(ClaseController.class).reporteCasiLlenas()).withSelfRel()));
    }

    @GetMapping("/reportes/entrenador/{entrenadorId}/conteo")
    @Operation(summary = "Reporte 4: Conteo por Entrenador", description = "Retorna el número total de clases activas que dicta un entrenador")
    public ResponseEntity<Long> reporteConteoEntrenador(@PathVariable Long entrenadorId) {
        return ResponseEntity.ok(service.contarClasesPorEntrenador(entrenadorId));
    }

    @GetMapping("/reportes/inactivas")
    @Operation(summary = "Reporte 5: Clases Inactivas", description = "Lista el historial de clases canceladas o que ya no están activas")
    public ResponseEntity<CollectionModel<EntityModel<ClaseResponseDTO>>> reporteInactivas() {
        List<EntityModel<ClaseResponseDTO>> clases = service.obtenerClasesInactivas().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clases,
                linkTo(methodOn(ClaseController.class).reporteInactivas()).withSelfRel()));
    }
}
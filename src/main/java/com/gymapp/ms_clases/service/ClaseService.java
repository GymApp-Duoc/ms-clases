package com.gymapp.ms_clases.service;

import com.gymapp.ms_clases.client.*;
import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.exception.BusinessException;
import com.gymapp.ms_clases.exception.RecursoNoEncontradoException;
import com.gymapp.ms_clases.model.Clase;
import com.gymapp.ms_clases.repository.ClaseRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaseService implements ClaseServiceInt {

    private final ClaseRepository repository;

    private final EntrenadorClient entrenadorClient;
    private final MiembroClient miembroClient;
    private final SuscripcionClient suscripcionClient;
    private final GamificacionClient gamificacionClient;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> listarTodas() {
        log.info("Consultando todas las clases");
        return repository.findAll().stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClaseResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::mapearADto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> buscarPorEntrenador(Long entrenadorId) {
        validarEntrenadorExterno(entrenadorId);
        return repository.findByEntrenadorId(entrenadorId).stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClaseResponseDTO crear(ClaseRequestDTO dto) {
        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BusinessException("Ya existe una clase con el nombre: " + dto.getNombre());
        }
        validarEntrenadorExterno(dto.getEntrenadorId());

        Clase clase = new Clase();
        clase.setNombre(dto.getNombre());
        clase.setDisciplina(dto.getDisciplina());
        clase.setCapacidad(dto.getCapacidad());
        clase.setEntrenadorId(dto.getEntrenadorId());
        clase.setActiva(true);

        return mapearADto(repository.save(clase));
    }

    @Override
    @Transactional
    public Optional<ClaseResponseDTO> actualizar(Long id, ClaseRequestDTO dto) {
        return repository.findById(id).map(existente -> {
            if (!existente.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                    repository.existsByNombreIgnoreCase(dto.getNombre())) {
                throw new BusinessException("Ya existe otra clase con el nombre: " + dto.getNombre());
            }

            validarEntrenadorExterno(dto.getEntrenadorId());

            existente.setNombre(dto.getNombre());
            existente.setDisciplina(dto.getDisciplina());
            existente.setCapacidad(dto.getCapacidad());
            existente.setEntrenadorId(dto.getEntrenadorId());
            return mapearADto(repository.save(existente));
        });
    }

    @Override
    @Transactional
    public Optional<ClaseResponseDTO> reducirCupo(Long id, Long miembroId) {
        return repository.findById(id).map(clase -> {
            validarMiembroSuscripcion(miembroId);

            if (!clase.isActiva()) {
                throw new BusinessException("Esta clase se encuentra inactiva.");
            }

            if (clase.getCapacidad() <= 0) {
                throw new BusinessException("Aforo completo para: " + clase.getNombre());
            }

            clase.setCapacidad(clase.getCapacidad() - 1);
            Clase claseGuardada = repository.save(clase);

            enviarGamificacionYNotificacion(miembroId, clase);

            return mapearADto(claseGuardada);
        });
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.findById(id).ifPresent(clase -> {
            clase.setActiva(false);
            repository.save(clase);
            log.info("Clase desactivada (borrado lógico): {}", id);
        });
    }


    private void validarEntrenadorExterno(Long id) {
        try {
            entrenadorClient.obtenerEntrenador(id);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Entrenador ID " + id + " no encontrado.");
        } catch (FeignException e) {
            log.error("Error comunicándose con el servicio de entrenadores", e);
            throw new BusinessException("Servicio de entrenadores no disponible en este momento.");
        }
    }

    private void validarMiembroSuscripcion(Long miembroId) {
        try {
            miembroClient.obtenerPorId(miembroId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Regla de negocio: El miembro ID " + miembroId + " no existe.");
        } catch (FeignException e) {
            log.error("Error comunicándose con el servicio de miembros", e);
            throw new BusinessException("Servicio de miembros no disponible en este momento.");
        }

        try {
            suscripcionClient.verificarEstado(miembroId);
        } catch (FeignException e) {
            log.error("Error comunicándose con el servicio de suscripciones o sin suscripción activa", e);
            throw new BusinessException("Regla de negocio: El miembro ID " + miembroId + " no tiene una suscripción activa.");
        }
    }

    private void enviarGamificacionYNotificacion(Long miembroId, Clase clase) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("miembroId", miembroId);
            evento.put("accion", "ASISTENCIA_CLASE");
            evento.put("puntosBase", 20);

            gamificacionClient.enviarEvento(evento);
            log.info("Evento de asistencia enviado a Gamificación exitosamente para el miembro {}", miembroId);
        } catch (Exception e) {
            log.error("Aviso: No se pudieron enviar los puntos a Gamificación. Detalle: {}", e.getMessage());
        }

        try {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("miembroId", miembroId);
            notificacion.put("titulo", "¡Reserva Confirmada!");
            notificacion.put("mensaje", "Te esperamos en la clase de " + clase.getDisciplina() + ". ¡Prepárate para sudar!");

            notificacionClient.enviarNotificacion(notificacion);
            log.info("Notificación de reserva enviada al miembro {}", miembroId);
        } catch (Exception e) {
            log.error("Aviso: No se pudo enviar la notificación de reserva. Detalle: {}", e.getMessage());
        }
    }

    private ClaseResponseDTO mapearADto(Clase clase) {
        return ClaseResponseDTO.builder()
                .id(clase.getId())
                .nombre(clase.getNombre())
                .disciplina(clase.getDisciplina())
                .capacidad(clase.getCapacidad())
                .entrenadorId(clase.getEntrenadorId())
                .activa(clase.isActiva())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerClasesDisponibles() {
        log.info("Generando reporte: Clases disponibles");
        return repository.findClasesDisponibles().stream()
                .map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerPorDisciplina(String disciplina) {
        log.info("Generando reporte: Clases de la disciplina {}", disciplina);
        return repository.findByDisciplinaActiva(disciplina).stream()
                .map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerClasesCasiLlenas() {
        log.info("Generando reporte: Clases casi llenas");
        return repository.findClasesCasiLlenas().stream()
                .map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarClasesPorEntrenador(Long entrenadorId) {
        log.info("Generando reporte: Conteo de clases activas para el entrenador {}", entrenadorId);
        validarEntrenadorExterno(entrenadorId); // Reutilizamos la validación Feign
        return repository.countClasesActivasPorEntrenador(entrenadorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerClasesInactivas() {
        log.info("Generando reporte: Clases inactivas o canceladas");
        return repository.findClasesInactivas().stream()
                .map(this::mapearADto).collect(Collectors.toList());
    }

}
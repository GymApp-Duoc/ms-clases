package com.gymapp.ms_clases.service;

import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.exception.BusinessException;
import com.gymapp.ms_clases.exception.RecursoNoEncontradoException;
import com.gymapp.ms_clases.model.Clase;
import com.gymapp.ms_clases.repository.ClaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaseService {

    private final ClaseRepository repository;
    private final RestTemplate restTemplate;

    @Value("${ms.entrenadores.url}")
    private String entrenadoresUrl;

    @Value("${ms.miembros.url}")
    private String miembrosUrl;

    @Value("${ms.suscripciones.url}")
    private String suscripcionesUrl;

    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> listarTodas() {
        log.info("Consultando todas las clases");
        return repository.findAll().stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClaseResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::mapearADto);
    }

    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> buscarPorEntrenador(Long entrenadorId) {
        validarEntrenadorExterno(entrenadorId);
        return repository.findByEntrenadorId(entrenadorId).stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Transactional
    public ClaseResponseDTO crear(ClaseRequestDTO dto) {
        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BusinessException("Ya existe una clase con el nombre: " + dto.getNombre());
        }
        validarEntrenadorExterno(dto.getEntrenadorId());
        Clase clase = new Clase(null, dto.getNombre(), dto.getDisciplina(), dto.getCapacidad(), dto.getEntrenadorId());
        return mapearADto(repository.save(clase));
    }

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

    @Transactional
    public Optional<ClaseResponseDTO> reducirCupo(Long id, Long miembroId) {
        return repository.findById(id).map(clase -> {
            validarMiembroSuscripcion(miembroId);
            if (clase.getCapacidad() <= 0) {
                throw new BusinessException("Aforo completo para: " + clase.getNombre());
            }
            clase.setCapacidad(clase.getCapacidad() - 1);
            return mapearADto(repository.save(clase));
        });
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
        log.info("Clase eliminada: {}", id);
    }

    private void validarEntrenadorExterno(Long id) {
        try {
            String url = entrenadoresUrl + "/api/entrenadores/" + id;
            restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Entrenador ID " + id + " no encontrado.");
        } catch (RestClientException e) {
            log.error("Error comunicándose con el servicio de entrenadores", e);
            throw new BusinessException("Servicio de entrenadores no disponible en este momento.");
        }
    }

    private void validarMiembroSuscripcion(Long miembroId) {
        try {
            String urlMiembro = miembrosUrl + "/api/miembros/" + miembroId;
            restTemplate.getForObject(urlMiembro, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Regla de negocio: El miembro ID " + miembroId + " no existe.");
        } catch (RestClientException e) {
            log.error("Error comunicándose con el servicio de miembros", e);
            throw new BusinessException("Servicio de miembros no disponible en este momento.");
        }

        try {
            String urlSuscripcion = suscripcionesUrl + "/api/suscripciones/miembro/" + miembroId + "/estado";
            restTemplate.getForObject(urlSuscripcion, String.class);
        } catch (HttpClientErrorException e) {
            throw new BusinessException("Regla de negocio: El miembro ID " + miembroId + " no tiene una suscripción activa.");
        } catch (RestClientException e) {
            log.error("Error comunicándose con el servicio de suscripciones", e);
            throw new BusinessException("Servicio de suscripciones no disponible en este momento.");
        }
    }

    private ClaseResponseDTO mapearADto(Clase clase) {
        return new ClaseResponseDTO(clase.getId(), clase.getNombre(), clase.getDisciplina(), clase.getCapacidad(), clase.getEntrenadorId());
    }
}
package com.gymapp.ms_clases.repository;

import com.gymapp.ms_clases.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByEntrenadorId(Long entrenadorId);
    boolean existsByNombreIgnoreCase(String nombre);
}

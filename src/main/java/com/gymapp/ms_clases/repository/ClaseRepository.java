package com.gymapp.ms_clases.repository;

import com.gymapp.ms_clases.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByEntrenadorId(Long entrenadorId);
    boolean existsByNombreIgnoreCase(String nombre);




    @Query("SELECT c FROM Clase c WHERE c.capacidad > 0 AND c.activa = true")
    List<Clase> findClasesDisponibles();


    @Query("SELECT c FROM Clase c WHERE LOWER(c.disciplina) = LOWER(:disciplina) AND c.activa = true")
    List<Clase> findByDisciplinaActiva(@Param("disciplina") String disciplina);


    @Query("SELECT c FROM Clase c WHERE c.capacidad BETWEEN 1 AND 5 AND c.activa = true ORDER BY c.capacidad ASC")
    List<Clase> findClasesCasiLlenas();


    @Query("SELECT COUNT(c) FROM Clase c WHERE c.entrenadorId = :entrenadorId AND c.activa = true")
    long countClasesActivasPorEntrenador(@Param("entrenadorId") Long entrenadorId);


    @Query("SELECT c FROM Clase c WHERE c.activa = false")
    List<Clase> findClasesInactivas();
}
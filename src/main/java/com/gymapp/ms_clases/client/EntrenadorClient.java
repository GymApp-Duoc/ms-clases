package com.gymapp.ms_clases.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "ms-entrenadores")
public interface EntrenadorClient {

    @GetMapping("/api/entrenadores/{id}")
    Object obtenerEntrenador(@PathVariable("id") Long id);

    @GetMapping("/api/entrenadores/disponibilidad/{id}")
    boolean verificarDisponibilidad(@PathVariable("id") Long id);
}
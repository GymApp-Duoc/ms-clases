package com.gymapp.ms_clases.assembler;

import com.gymapp.ms_clases.controller.ClaseController;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClaseModelAssembler implements RepresentationModelAssembler<ClaseResponseDTO, EntityModel<ClaseResponseDTO>> {

    @Override
    public EntityModel<ClaseResponseDTO> toModel(ClaseResponseDTO dto) {
        return EntityModel.of(dto,

                linkTo(methodOn(ClaseController.class).obtenerPorId(dto.getId())).withSelfRel(),

                linkTo(methodOn(ClaseController.class).obtenerTodas()).withRel("clases")
        );
    }
}
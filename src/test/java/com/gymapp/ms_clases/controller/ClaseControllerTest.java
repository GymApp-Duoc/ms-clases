package com.gymapp.ms_clases.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymapp.ms_clases.assembler.ClaseModelAssembler;
import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.service.ClaseServiceInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClaseServiceInt service;

    @MockitoBean
    private ClaseModelAssembler assembler;
    private ClaseResponseDTO responseMock;

    @BeforeEach
    void setUp() {
        responseMock = new ClaseResponseDTO(1L, "CrossFit Elite", "CrossFit", 15, 1L, true);
        when(assembler.toModel(any(ClaseResponseDTO.class))).thenAnswer(invocation ->
                EntityModel.of((ClaseResponseDTO) invocation.getArgument(0)));
    }

    @Test
    void obtenerTodas_Exito() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(responseMock));

        mockMvc.perform(get("/api/clases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.claseResponseDTOList[0].nombre").value("CrossFit Elite"));
    }

    @Test
    void crearClase_Exito() throws Exception {
        ClaseRequestDTO request = new ClaseRequestDTO("CrossFit Elite", "CrossFit", 15, 1L);
        when(service.crear(any(ClaseRequestDTO.class))).thenReturn(responseMock);

        mockMvc.perform(post("/api/clases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("CrossFit Elite"));
    }
}
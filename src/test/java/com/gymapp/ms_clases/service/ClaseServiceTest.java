package com.gymapp.ms_clases.service;

import com.gymapp.ms_clases.client.*;
import com.gymapp.ms_clases.dto.ClaseRequestDTO;
import com.gymapp.ms_clases.dto.ClaseResponseDTO;
import com.gymapp.ms_clases.exception.BusinessException;
import com.gymapp.ms_clases.model.Clase;
import com.gymapp.ms_clases.repository.ClaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaseServiceTest {

    @Mock
    private ClaseRepository repository;
    @Mock
    private EntrenadorClient entrenadorClient;
    @Mock
    private MiembroClient miembroClient;
    @Mock
    private SuscripcionClient suscripcionClient;
    @Mock
    private GamificacionClient gamificacionClient;
    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private ClaseService service;

    private Clase claseMock;
    private ClaseRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        claseMock = new Clase(1L, "Yoga PM", "Yoga", 20, 1L, true);
        requestMock = new ClaseRequestDTO("Yoga PM", "Yoga", 20, 1L);
    }

    @Test
    void crearClase_Exito() {

        when(repository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(entrenadorClient.obtenerEntrenador(anyLong())).thenReturn(new Object());
        when(repository.save(any(Clase.class))).thenReturn(claseMock);


        ClaseResponseDTO resultado = service.crear(requestMock);


        assertNotNull(resultado);
        assertEquals("Yoga PM", resultado.getNombre());
        verify(repository).save(any(Clase.class));
    }

    @Test
    void crearClase_FallaPorNombreDuplicado() {
        // Arrange
        when(repository.existsByNombreIgnoreCase(anyString())).thenReturn(true);


        BusinessException exception = assertThrows(BusinessException.class, () -> service.crear(requestMock));
        assertTrue(exception.getMessage().contains("Ya existe una clase"));
        verify(repository, never()).save(any(Clase.class));
    }

    @Test
    void reducirCupo_Exito() {

        Long claseId = 1L;
        Long miembroId = 100L;
        when(repository.findById(claseId)).thenReturn(Optional.of(claseMock));
        when(miembroClient.obtenerPorId(miembroId)).thenReturn(new Object());
        when(suscripcionClient.verificarEstado(miembroId)).thenReturn(new Object());
        when(repository.save(any(Clase.class))).thenReturn(claseMock);


        Optional<ClaseResponseDTO> resultado = service.reducirCupo(claseId, miembroId);


        assertTrue(resultado.isPresent());
        assertEquals(19, claseMock.getCapacidad()); // El cupo original era 20, debe bajar a 19
        verify(gamificacionClient).enviarEvento(anyMap());
        verify(notificacionClient).enviarNotificacion(anyMap());
    }
}
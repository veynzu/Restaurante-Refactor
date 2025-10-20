package com.example.demo.controller;

import com.example.demo.entity.Estado;
import com.example.demo.service.EstadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para EstadoController
 * Usa @WebMvcTest para probar solo la capa de controlador
 */
@WebMvcTest(EstadoController.class)
class EstadoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EstadoService estadoService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Estado estado;
    
    @BeforeEach
    void setUp() {
        estado = new Estado("DISPONIBLE");
        estado.setIdEstado(1);
    }
    
    @Test
    void testObtenerTodosLosEstados() throws Exception {
        // Arrange
        List<Estado> estados = Arrays.asList(estado);
        when(estadoService.obtenerEstadosOrdenados()).thenReturn(estados);
        
        // Act & Assert
        mockMvc.perform(get("/api/estados"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nombre").value("DISPONIBLE"));
    }
    
    @Test
    void testObtenerEstadoPorId() throws Exception {
        // Arrange
        when(estadoService.obtenerEstadoPorId(1)).thenReturn(Optional.of(estado));
        
        // Act & Assert
        mockMvc.perform(get("/api/estados/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("DISPONIBLE"));
    }
    
    @Test
    void testObtenerEstadoPorIdNoExiste() throws Exception {
        // Arrange
        when(estadoService.obtenerEstadoPorId(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/estados/999"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void testCrearEstado() throws Exception {
        // Arrange
        Estado nuevoEstado = new Estado("PENDIENTE");
        when(estadoService.crearEstado(any(Estado.class))).thenReturn(estado);
        
        // Act & Assert
        mockMvc.perform(post("/api/estados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoEstado)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("DISPONIBLE"));
    }
    
    @Test
    void testActualizarEstado() throws Exception {
        // Arrange
        Estado estadoActualizado = new Estado("MODIFICADO");
        estadoActualizado.setIdEstado(1);
        when(estadoService.actualizarEstado(anyInt(), any(Estado.class))).thenReturn(estadoActualizado);
        
        // Act & Assert
        mockMvc.perform(put("/api/estados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadoActualizado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("MODIFICADO"));
    }
    
    @Test
    void testEliminarEstado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/estados/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void testBuscarEstados() throws Exception {
        // Arrange
        List<Estado> estados = Arrays.asList(estado);
        when(estadoService.buscarEstadosPorTexto("DISP")).thenReturn(estados);
        
        // Act & Assert
        mockMvc.perform(get("/api/estados/buscar?texto=DISP"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nombre").value("DISPONIBLE"));
    }
    
    @Test
    void testContarEstados() throws Exception {
        // Arrange
        when(estadoService.contarEstados()).thenReturn(5L);
        
        // Act & Assert
        mockMvc.perform(get("/api/estados/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(5));
    }
}

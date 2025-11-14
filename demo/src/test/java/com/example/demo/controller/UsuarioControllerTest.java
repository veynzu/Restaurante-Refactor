package com.example.demo.controller;

import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para UsuarioController
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UsuarioService usuarioService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Rol rolAdmin;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        rolAdmin = new Rol("ADMINISTRADOR");
        rolAdmin.setIdRol(1);
        
        usuario = new Usuario("admin001", "Juan Perez", "admin@test.com", "pass123", rolAdmin);
    }
    
    @Test
    void testObtenerTodosLosUsuarios() throws Exception {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);
        
        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nombre").value("Juan Perez"));
    }
    
    @Test
    void testObtenerUsuarioPorId() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId("admin001")).thenReturn(Optional.of(usuario));
        
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/admin001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Juan Perez"))
            .andExpect(jsonPath("$.email").value("admin@test.com"));
    }
    
    @Test
    void testObtenerUsuarioPorIdNoExiste() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId("noexiste")).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/noexiste"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void testCrearUsuario() throws Exception {
        // Arrange
        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);
        
        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }
    
    @Test
    void testAutenticarUsuario() throws Exception {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "admin@test.com");
        credentials.put("password", "pass123");
        
        when(usuarioService.autenticarUsuario(anyString(), anyString())).thenReturn(usuario);
        
        // Act & Assert
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Juan Perez"))
            .andExpect(jsonPath("$.email").value("admin@test.com"));
    }
    
    @Test
    void testAutenticarUsuarioCredencialesInvalidas() throws Exception {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "admin@test.com");
        credentials.put("password", "wrongpass");
        
        when(usuarioService.autenticarUsuario(anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Credenciales inválidas"));
        
        // Act & Assert
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void testBuscarUsuarios() throws Exception {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.buscarUsuariosPorNombre("Juan")).thenReturn(usuarios);
        
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/buscar?nombre=Juan"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nombre").value("Juan Perez"));
    }
}

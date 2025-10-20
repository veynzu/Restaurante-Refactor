package com.example.demo.controller;

import com.example.demo.entity.Categoria;
import com.example.demo.entity.Producto;
import com.example.demo.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para ProductoController
 */
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductoService productoService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Categoria categoria;
    private Producto producto;
    
    @BeforeEach
    void setUp() {
        categoria = new Categoria("ENTRADAS");
        categoria.setIdCategoria(1);
        
        producto = new Producto("Ensalada", new BigDecimal("12.50"), 50, categoria);
        producto.setIdProducto(1);
    }
    
    @Test
    void testObtenerTodosLosProductos() throws Exception {
        // Arrange
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.obtenerTodosLosProductos()).thenReturn(productos);
        
        // Act & Assert
        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nombre").value("Ensalada"));
    }
    
    @Test
    void testObtenerProductoPorId() throws Exception {
        // Arrange
        when(productoService.obtenerProductoPorId(1)).thenReturn(Optional.of(producto));
        
        // Act & Assert
        mockMvc.perform(get("/api/productos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Ensalada"))
            .andExpect(jsonPath("$.precio").value(12.50));
    }
    
    @Test
    void testCrearProducto() throws Exception {
        // Arrange
        when(productoService.crearProducto(any(Producto.class))).thenReturn(producto);
        
        // Act & Assert
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Ensalada"));
    }
    
    @Test
    void testObtenerProductosActivos() throws Exception {
        // Arrange
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.obtenerProductosActivos()).thenReturn(productos);
        
        // Act & Assert
        mockMvc.perform(get("/api/productos/activos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].estado").value(true));
    }
    
    @Test
    void testActualizarStock() throws Exception {
        // Arrange
        Map<String, Integer> request = new HashMap<>();
        request.put("stock", 100);
        
        producto.setStock(100);
        when(productoService.actualizarStock(anyInt(), anyInt())).thenReturn(producto);
        
        // Act & Assert
        mockMvc.perform(put("/api/productos/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock").value(100));
    }
    
    @Test
    void testActivarProducto() throws Exception {
        // Arrange
        when(productoService.activarProducto(1)).thenReturn(producto);
        
        // Act & Assert
        mockMvc.perform(put("/api/productos/1/activar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value(true));
    }
    
    @Test
    void testDesactivarProducto() throws Exception {
        // Arrange
        producto.setEstado(false);
        when(productoService.desactivarProducto(1)).thenReturn(producto);
        
        // Act & Assert
        mockMvc.perform(put("/api/productos/1/desactivar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value(false));
    }
}

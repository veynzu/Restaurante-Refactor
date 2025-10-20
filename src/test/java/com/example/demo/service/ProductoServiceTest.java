package com.example.demo.service;

import com.example.demo.entity.Categoria;
import com.example.demo.entity.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para ProductoService
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    
    @Mock
    private ProductoRepository productoRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks
    private ProductoService productoService;
    
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
    void testObtenerTodosLosProductos() {
        // Arrange
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);
        
        // Act
        List<Producto> resultado = productoService.obtenerTodosLosProductos();
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Ensalada");
    }
    
    @Test
    void testObtenerProductoPorId() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        
        // Act
        Optional<Producto> resultado = productoService.obtenerProductoPorId(1);
        
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Ensalada");
    }
    
    @Test
    void testCrearProducto() {
        // Arrange
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        // Act
        Producto resultado = productoService.crearProducto(producto);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Ensalada");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    
    @Test
    void testCrearProductoNombreDuplicado() {
        // Arrange
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByNombre("Ensalada")).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> productoService.crearProducto(producto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ya existe un producto");
    }
    
    @Test
    void testActivarProducto() {
        // Arrange
        producto.setEstado(false);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        // Act
        Producto resultado = productoService.activarProducto(1);
        
        // Assert
        assertThat(resultado.getEstado()).isTrue();
    }
    
    @Test
    void testDesactivarProducto() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        // Act
        Producto resultado = productoService.desactivarProducto(1);
        
        // Assert
        assertThat(resultado.getEstado()).isFalse();
    }
    
    @Test
    void testActualizarStock() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Producto resultado = productoService.actualizarStock(1, 100);
        
        // Assert
        assertThat(resultado.getStock()).isEqualTo(100);
    }
    
    @Test
    void testReducirStock() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Producto resultado = productoService.reducirStock(1, 10);
        
        // Assert
        assertThat(resultado.getStock()).isEqualTo(40);
    }
    
    @Test
    void testReducirStockInsuficiente() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        
        // Act & Assert
        assertThatThrownBy(() -> productoService.reducirStock(1, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Stock insuficiente");
    }
    
    @Test
    void testAumentarStock() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Producto resultado = productoService.aumentarStock(1, 20);
        
        // Assert
        assertThat(resultado.getStock()).isEqualTo(70);
    }
    
    @Test
    void testObtenerProductosActivos() {
        // Arrange
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByEstadoTrue()).thenReturn(productos);
        
        // Act
        List<Producto> resultado = productoService.obtenerProductosActivos();
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isTrue();
    }
}

package com.example.demo.repository;

import com.example.demo.entity.Categoria;
import com.example.demo.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para ProductoRepository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProductoRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    private Categoria categoria1;
    private Categoria categoria2;
    private Producto producto1;
    private Producto producto2;
    
    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
        
        // Crear categorías
        categoria1 = new Categoria("ENTRADAS");
        categoria2 = new Categoria("PLATOS FUERTES");
        entityManager.persist(categoria1);
        entityManager.persist(categoria2);
        
        // Crear productos
        producto1 = new Producto("Ensalada", new BigDecimal("12.50"), 50, categoria1);
        producto2 = new Producto("Lomo Saltado", new BigDecimal("25.00"), 10, categoria2);
        
        entityManager.persist(producto1);
        entityManager.persist(producto2);
        entityManager.flush();
    }
    
    @Test
    void testGuardarProducto() {
        Producto nuevoProducto = new Producto("Pizza", new BigDecimal("20.00"), 30, categoria1);
        Producto guardado = productoRepository.save(nuevoProducto);
        
        assertThat(guardado.getIdProducto()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Pizza");
        assertThat(guardado.getPrecio()).isEqualByComparingTo("20.00");
    }
    
    @Test
    void testBuscarProductoPorNombre() {
        Optional<Producto> encontrado = productoRepository.findByNombre("Ensalada");
        
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPrecio()).isEqualByComparingTo("12.50");
    }
    
    @Test
    void testBuscarProductosPorCategoria() {
        List<Producto> productos = productoRepository.findByCategoria(categoria1);
        
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Ensalada");
    }
    
    @Test
    void testBuscarProductosActivos() {
        List<Producto> activos = productoRepository.findByEstadoTrue();
        
        assertThat(activos).hasSize(2);
    }
    
    @Test
    void testBuscarProductosPorNombreContenido() {
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase("LOMO");
        
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Lomo Saltado");
    }
    
    @Test
    void testBuscarProductosPorRangoPrecio() {
        List<Producto> productos = productoRepository.findByPrecioBetween(
            new BigDecimal("10.00"), 
            new BigDecimal("20.00")
        );
        
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Ensalada");
    }
    
    @Test
    void testBuscarProductosConStockBajo() {
        List<Producto> productos = productoRepository.findByStockLessThan(20);
        
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Lomo Saltado");
        assertThat(productos.get(0).getStock()).isEqualTo(10);
    }
    
    @Test
    void testContarProductosPorCategoria() {
        long count = productoRepository.countByCategoria(categoria1);
        
        assertThat(count).isEqualTo(1);
    }
    
    @Test
    void testExisteProductoPorNombre() {
        boolean existe = productoRepository.existsByNombre("Ensalada");
        
        assertThat(existe).isTrue();
    }
}

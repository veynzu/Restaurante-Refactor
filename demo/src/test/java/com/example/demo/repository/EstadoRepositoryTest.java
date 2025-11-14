package com.example.demo.repository;

import com.example.demo.entity.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para EstadoRepository
 * Prueba las operaciones CRUD y consultas personalizadas
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EstadoRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    private Estado estado1;
    private Estado estado2;
    
    @BeforeEach
    void setUp() {
        // Limpiar base de datos
        estadoRepository.deleteAll();
        
        // Crear datos de prueba
        estado1 = new Estado("DISPONIBLE");
        estado2 = new Estado("OCUPADO");
        
        entityManager.persist(estado1);
        entityManager.persist(estado2);
        entityManager.flush();
    }
    
    @Test
    void testGuardarEstado() {
        // Arrange
        Estado nuevoEstado = new Estado("PENDIENTE");
        
        // Act
        Estado guardado = estadoRepository.save(nuevoEstado);
        
        // Assert
        assertThat(guardado.getIdEstado()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("PENDIENTE");
    }
    
    @Test
    void testBuscarEstadoPorId() {
        // Act
        Optional<Estado> encontrado = estadoRepository.findById(estado1.getIdEstado());
        
        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testBuscarEstadoPorNombre() {
        // Act
        Optional<Estado> encontrado = estadoRepository.findByNombre("DISPONIBLE");
        
        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testBuscarEstadoPorNombreNoExiste() {
        // Act
        Optional<Estado> encontrado = estadoRepository.findByNombre("INEXISTENTE");
        
        // Assert
        assertThat(encontrado).isEmpty();
    }
    
    @Test
    void testBuscarEstadosOrdenados() {
        // Act
        List<Estado> estados = estadoRepository.findAllByOrderByNombreAsc();
        
        // Assert
        assertThat(estados).hasSize(2);
        assertThat(estados.get(0).getNombre()).isEqualTo("DISPONIBLE");
        assertThat(estados.get(1).getNombre()).isEqualTo("OCUPADO");
    }
    
    @Test
    void testBuscarEstadosPorTexto() {
        // Act
        List<Estado> estados = estadoRepository.findByNombreContaining("DISP");
        
        // Assert
        assertThat(estados).hasSize(1);
        assertThat(estados.get(0).getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testExisteEstadoPorNombre() {
        // Act
        boolean existe = estadoRepository.existsByNombre("DISPONIBLE");
        
        // Assert
        assertThat(existe).isTrue();
    }
    
    @Test
    void testNoExisteEstadoPorNombre() {
        // Act
        boolean existe = estadoRepository.existsByNombre("INEXISTENTE");
        
        // Assert
        assertThat(existe).isFalse();
    }
    
    @Test
    void testActualizarEstado() {
        // Arrange
        estado1.setNombre("MODIFICADO");
        
        // Act
        Estado actualizado = estadoRepository.save(estado1);
        
        // Assert
        assertThat(actualizado.getNombre()).isEqualTo("MODIFICADO");
    }
    
    @Test
    void testEliminarEstado() {
        // Arrange
        Integer id = estado1.getIdEstado();
        
        // Act
        estadoRepository.delete(estado1);
        
        // Assert
        assertThat(estadoRepository.findById(id)).isEmpty();
    }
    
    @Test
    void testContarEstados() {
        // Act
        long count = estadoRepository.count();
        
        // Assert
        assertThat(count).isEqualTo(2);
    }
}

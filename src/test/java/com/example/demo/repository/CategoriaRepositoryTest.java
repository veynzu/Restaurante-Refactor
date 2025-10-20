package com.example.demo.repository;

import com.example.demo.entity.Categoria;
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
 * Tests para CategoriaRepository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CategoriaRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    private Categoria entradas;
    private Categoria platosFuertes;
    
    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        
        entradas = new Categoria("ENTRADAS");
        platosFuertes = new Categoria("PLATOS FUERTES");
        
        entityManager.persist(entradas);
        entityManager.persist(platosFuertes);
        entityManager.flush();
    }
    
    @Test
    void testGuardarCategoria() {
        Categoria nuevaCategoria = new Categoria("BEBIDAS");
        Categoria guardada = categoriaRepository.save(nuevaCategoria);
        
        assertThat(guardada.getIdCategoria()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("BEBIDAS");
    }
    
    @Test
    void testBuscarCategoriaPorNombre() {
        Optional<Categoria> encontrada = categoriaRepository.findByNombre("ENTRADAS");
        
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNombre()).isEqualTo("ENTRADAS");
    }
    
    @Test
    void testBuscarCategoriasOrdenadas() {
        List<Categoria> categorias = categoriaRepository.findAllByOrderByNombreAsc();
        
        assertThat(categorias).hasSize(2);
        assertThat(categorias.get(0).getNombre()).isEqualTo("ENTRADAS");
    }
    
    @Test
    void testBuscarCategoriasPorTexto() {
        List<Categoria> categorias = categoriaRepository.findByNombreContaining("PLATOS");
        
        assertThat(categorias).hasSize(1);
        assertThat(categorias.get(0).getNombre()).isEqualTo("PLATOS FUERTES");
    }
    
    @Test
    void testExisteCategoriaPorNombre() {
        boolean existe = categoriaRepository.existsByNombre("ENTRADAS");
        
        assertThat(existe).isTrue();
    }
    
    @Test
    void testContarCategorias() {
        long count = categoriaRepository.count();
        
        assertThat(count).isEqualTo(2);
    }
}

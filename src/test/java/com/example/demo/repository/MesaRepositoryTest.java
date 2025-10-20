package com.example.demo.repository;

import com.example.demo.entity.Estado;
import com.example.demo.entity.Mesa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para MesaRepository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MesaRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    private Estado estadoDisponible;
    private Estado estadoOcupado;
    private Mesa mesa1;
    private Mesa mesa2;
    
    @BeforeEach
    void setUp() {
        mesaRepository.deleteAll();
        estadoRepository.deleteAll();
        
        // Crear estados
        estadoDisponible = new Estado("DISPONIBLE");
        estadoOcupado = new Estado("OCUPADO");
        entityManager.persist(estadoDisponible);
        entityManager.persist(estadoOcupado);
        
        // Crear mesas
        mesa1 = new Mesa(4, "Ventana", estadoDisponible);
        mesa2 = new Mesa(2, "Terraza", estadoOcupado);
        
        entityManager.persist(mesa1);
        entityManager.persist(mesa2);
        entityManager.flush();
    }
    
    @Test
    void testGuardarMesa() {
        Mesa nuevaMesa = new Mesa(6, "Interior", estadoDisponible);
        Mesa guardada = mesaRepository.save(nuevaMesa);
        
        assertThat(guardada.getIdMesa()).isNotNull();
        assertThat(guardada.getCapacidad()).isEqualTo(6);
    }
    
    @Test
    void testBuscarMesasPorEstado() {
        List<Mesa> mesasDisponibles = mesaRepository.findByEstado(estadoDisponible);
        
        assertThat(mesasDisponibles).hasSize(1);
        assertThat(mesasDisponibles.get(0).getCapacidad()).isEqualTo(4);
    }
    
    @Test
    void testBuscarMesasDisponibles() {
        List<Mesa> mesasDisponibles = mesaRepository.findMesasDisponibles();
        
        assertThat(mesasDisponibles).hasSize(1);
        assertThat(mesasDisponibles.get(0).getEstado().getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testBuscarMesasPorUbicacion() {
        List<Mesa> mesas = mesaRepository.findByUbicacionContainingIgnoreCase("VENT");
        
        assertThat(mesas).hasSize(1);
        assertThat(mesas.get(0).getUbicacion()).isEqualTo("Ventana");
    }
    
    @Test
    void testBuscarMesasPorCapacidadMinima() {
        List<Mesa> mesas = mesaRepository.findByCapacidadGreaterThanEqual(4);
        
        assertThat(mesas).hasSize(1);
        assertThat(mesas.get(0).getCapacidad()).isEqualTo(4);
    }
    
    @Test
    void testBuscarMesasDisponiblesPorCapacidad() {
        List<Mesa> mesas = mesaRepository.findByEstadoAndCapacidadGreaterThanEqual(estadoDisponible, 2);
        
        assertThat(mesas).hasSize(1);
        assertThat(mesas.get(0).getEstado().getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testContarMesasPorEstado() {
        long count = mesaRepository.countByEstado(estadoDisponible);
        
        assertThat(count).isEqualTo(1);
    }
}

package com.example.demo.repository;

import com.example.demo.entity.Rol;
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
 * Tests para RolRepository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RolRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private RolRepository rolRepository;
    
    private Rol rolAdmin;
    private Rol rolMesero;
    
    @BeforeEach
    void setUp() {
        rolRepository.deleteAll();
        
        rolAdmin = new Rol("ADMINISTRADOR");
        rolMesero = new Rol("MESERO");
        
        entityManager.persist(rolAdmin);
        entityManager.persist(rolMesero);
        entityManager.flush();
    }
    
    @Test
    void testGuardarRol() {
        Rol nuevoRol = new Rol("COCINERO");
        Rol guardado = rolRepository.save(nuevoRol);
        
        assertThat(guardado.getIdRol()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("COCINERO");
    }
    
    @Test
    void testBuscarRolPorNombre() {
        Optional<Rol> encontrado = rolRepository.findByNombre("ADMINISTRADOR");
        
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("ADMINISTRADOR");
    }
    
    @Test
    void testBuscarRolesOrdenados() {
        List<Rol> roles = rolRepository.findAllByOrderByNombreAsc();
        
        assertThat(roles).hasSize(2);
        assertThat(roles.get(0).getNombre()).isEqualTo("ADMINISTRADOR");
    }
    
    @Test
    void testExisteRolPorNombre() {
        boolean existe = rolRepository.existsByNombre("MESERO");
        
        assertThat(existe).isTrue();
    }
    
    @Test
    void testContarRoles() {
        long count = rolRepository.count();
        
        assertThat(count).isEqualTo(2);
    }
}

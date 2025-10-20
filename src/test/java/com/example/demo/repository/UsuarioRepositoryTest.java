package com.example.demo.repository;

import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
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
 * Tests para UsuarioRepository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UsuarioRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    private Rol rolAdmin;
    private Rol rolMesero;
    private Usuario usuario1;
    private Usuario usuario2;
    
    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
        
        // Crear roles
        rolAdmin = new Rol("ADMINISTRADOR");
        rolMesero = new Rol("MESERO");
        entityManager.persist(rolAdmin);
        entityManager.persist(rolMesero);
        
        // Crear usuarios
        usuario1 = new Usuario("admin001", "Juan Perez", "admin@test.com", "pass123", rolAdmin);
        usuario2 = new Usuario("mesero001", "Maria Lopez", "mesero@test.com", "pass123", rolMesero);
        
        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.flush();
    }
    
    @Test
    void testGuardarUsuario() {
        Usuario nuevoUsuario = new Usuario("test001", "Test User", "test@test.com", "pass123", rolAdmin);
        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        
        assertThat(guardado.getIdUsuario()).isEqualTo("test001");
        assertThat(guardado.getNombre()).isEqualTo("Test User");
    }
    
    @Test
    void testBuscarUsuarioPorEmail() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("admin@test.com");
        
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Juan Perez");
    }
    
    @Test
    void testBuscarUsuariosPorRol() {
        List<Usuario> meseros = usuarioRepository.findByRol(rolMesero);
        
        assertThat(meseros).hasSize(1);
        assertThat(meseros.get(0).getNombre()).isEqualTo("Maria Lopez");
    }
    
    @Test
    void testBuscarUsuariosPorNombre() {
        List<Usuario> usuarios = usuarioRepository.findByNombreContainingIgnoreCase("Maria");
        
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("mesero@test.com");
    }
    
    @Test
    void testExisteUsuarioPorEmail() {
        boolean existe = usuarioRepository.existsByEmail("admin@test.com");
        
        assertThat(existe).isTrue();
    }
    
    @Test
    void testBuscarMeseros() {
        List<Usuario> meseros = usuarioRepository.findMeseros();
        
        assertThat(meseros).hasSize(1);
        assertThat(meseros.get(0).getRol().getNombre()).isEqualTo("MESERO");
    }
    
    @Test
    void testContarUsuarios() {
        long count = usuarioRepository.count();
        
        assertThat(count).isEqualTo(2);
    }
}

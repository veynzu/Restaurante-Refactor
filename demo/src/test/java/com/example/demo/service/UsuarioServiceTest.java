package com.example.demo.service;

import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.TelefonoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.UsuarioTelefonoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para UsuarioService
 * Usa Mockito para simular dependencias
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private RolRepository rolRepository;
    
    @Mock
    private TelefonoRepository telefonoRepository;
    
    @Mock
    private UsuarioTelefonoRepository usuarioTelefonoRepository;
    
    @InjectMocks
    private UsuarioService usuarioService;
    
    private Rol rolAdmin;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        rolAdmin = new Rol("ADMINISTRADOR");
        rolAdmin.setIdRol(1);
        
        usuario = new Usuario("admin001", "Juan Perez", "admin@test.com", "pass123", rolAdmin);
    }
    
    @Test
    void testObtenerTodosLosUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        
        // Act
        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan Perez");
        verify(usuarioRepository, times(1)).findAll();
    }
    
    @Test
    void testObtenerUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById("admin001")).thenReturn(Optional.of(usuario));
        
        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId("admin001");
        
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan Perez");
    }
    
    @Test
    void testObtenerUsuarioPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        
        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("admin@test.com");
        
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("admin@test.com");
    }
    
    @Test
    void testCrearUsuario() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        // Act
        Usuario resultado = usuarioService.crearUsuario(usuario);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Perez");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
    
    @Test
    void testCrearUsuarioEmailDuplicado() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepository.existsByEmail("admin@test.com")).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> usuarioService.crearUsuario(usuario))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("email ya está registrado");
        
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    
    @Test
    void testAutenticarUsuarioExitoso() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        
        // Act
        Usuario resultado = usuarioService.autenticarUsuario("admin@test.com", "pass123");
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("admin@test.com");
    }
    
    @Test
    void testAutenticarUsuarioCredencialesInvalidas() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        
        // Act & Assert
        assertThatThrownBy(() -> usuarioService.autenticarUsuario("admin@test.com", "wrongpass"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Credenciales inválidas");
    }
    
    @Test
    void testAutenticarUsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> usuarioService.autenticarUsuario("noexiste@test.com", "pass"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Credenciales inválidas");
    }
    
    @Test
    void testBuscarUsuariosPorNombre() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findByNombreContainingIgnoreCase("Juan")).thenReturn(usuarios);
        
        // Act
        List<Usuario> resultado = usuarioService.buscarUsuariosPorNombre("Juan");
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).contains("Juan");
    }
    
    @Test
    void testExisteUsuarioPorEmail() {
        // Arrange
        when(usuarioRepository.existsByEmail("admin@test.com")).thenReturn(true);
        
        // Act
        boolean existe = usuarioService.existeUsuarioPorEmail("admin@test.com");
        
        // Assert
        assertThat(existe).isTrue();
    }
}

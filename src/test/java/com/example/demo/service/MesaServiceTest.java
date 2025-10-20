package com.example.demo.service;

import com.example.demo.entity.Estado;
import com.example.demo.entity.Mesa;
import com.example.demo.repository.EstadoRepository;
import com.example.demo.repository.MesaRepository;
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
import static org.mockito.Mockito.*;

/**
 * Tests para MesaService
 */
@ExtendWith(MockitoExtension.class)
class MesaServiceTest {
    
    @Mock
    private MesaRepository mesaRepository;
    
    @Mock
    private EstadoRepository estadoRepository;
    
    @InjectMocks
    private MesaService mesaService;
    
    private Estado estadoDisponible;
    private Estado estadoOcupado;
    private Mesa mesa;
    
    @BeforeEach
    void setUp() {
        estadoDisponible = new Estado("DISPONIBLE");
        estadoDisponible.setIdEstado(1);
        
        estadoOcupado = new Estado("OCUPADO");
        estadoOcupado.setIdEstado(2);
        
        mesa = new Mesa(4, "Ventana", estadoDisponible);
        mesa.setIdMesa(1);
    }
    
    @Test
    void testObtenerTodasLasMesas() {
        // Arrange
        List<Mesa> mesas = Arrays.asList(mesa);
        when(mesaRepository.findAllByOrderByIdMesaAsc()).thenReturn(mesas);
        
        // Act
        List<Mesa> resultado = mesaService.obtenerTodasLasMesas();
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUbicacion()).isEqualTo("Ventana");
    }
    
    @Test
    void testObtenerMesaPorId() {
        // Arrange
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesa));
        
        // Act
        Optional<Mesa> resultado = mesaService.obtenerMesaPorId(1);
        
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCapacidad()).isEqualTo(4);
    }
    
    @Test
    void testCrearMesa() {
        // Arrange
        when(estadoRepository.findById(1)).thenReturn(Optional.of(estadoDisponible));
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesa);
        
        // Act
        Mesa resultado = mesaService.crearMesa(mesa);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCapacidad()).isEqualTo(4);
        verify(mesaRepository, times(1)).save(any(Mesa.class));
    }
    
    @Test
    void testOcuparMesa() {
        // Arrange
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesa));
        when(estadoRepository.findByNombre("OCUPADO")).thenReturn(Optional.of(estadoOcupado));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Mesa resultado = mesaService.ocuparMesa(1);
        
        // Assert
        assertThat(resultado.getEstado().getNombre()).isEqualTo("OCUPADO");
    }
    
    @Test
    void testLiberarMesa() {
        // Arrange
        mesa.setEstado(estadoOcupado);
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesa));
        when(estadoRepository.findByNombre("DISPONIBLE")).thenReturn(Optional.of(estadoDisponible));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Mesa resultado = mesaService.liberarMesa(1);
        
        // Assert
        assertThat(resultado.getEstado().getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testObtenerMesasDisponibles() {
        // Arrange
        List<Mesa> mesas = Arrays.asList(mesa);
        when(estadoRepository.findByNombre("DISPONIBLE")).thenReturn(Optional.of(estadoDisponible));
        when(mesaRepository.findByEstado(estadoDisponible)).thenReturn(mesas);
        
        // Act
        List<Mesa> resultado = mesaService.obtenerMesasDisponibles();
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado().getNombre()).isEqualTo("DISPONIBLE");
    }
    
    @Test
    void testCambiarEstadoMesa() {
        // Arrange
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesa));
        when(estadoRepository.findById(2)).thenReturn(Optional.of(estadoOcupado));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Mesa resultado = mesaService.cambiarEstadoMesa(1, 2);
        
        // Assert
        assertThat(resultado.getEstado().getNombre()).isEqualTo("OCUPADO");
    }
    
    @Test
    void testCambiarEstadoMesaNoExiste() {
        // Arrange
        when(mesaRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> mesaService.cambiarEstadoMesa(999, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mesa no encontrada");
    }
}

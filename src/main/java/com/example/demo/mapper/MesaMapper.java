package com.example.demo.mapper;

import com.example.demo.dto.response.EstadoDTO;
import com.example.demo.dto.response.MesaResponseDTO;
import com.example.demo.dto.response.MesaSimpleDTO;
import com.example.demo.entity.Mesa;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Mesa Entity y DTOs
 */
@Component
public class MesaMapper {
    
    /**
     * Convierte Mesa entity a MesaResponseDTO
     */
    public MesaResponseDTO toResponseDTO(Mesa mesa) {
        if (mesa == null) {
            return null;
        }
        
        return MesaResponseDTO.builder()
                .idMesa(mesa.getIdMesa() != null ? mesa.getIdMesa().longValue() : null)
                .ubicacion(mesa.getUbicacion())
                .capacidad(mesa.getCapacidad())
                .estado(toEstadoDTO(mesa.getEstado()))
                .build();
    }
    
    /**
     * Convierte Mesa entity a MesaSimpleDTO
     */
    public MesaSimpleDTO toSimpleDTO(Mesa mesa) {
        if (mesa == null) {
            return null;
        }
        
        return MesaSimpleDTO.builder()
                .idMesa(mesa.getIdMesa() != null ? mesa.getIdMesa().longValue() : null)
                .ubicacion(mesa.getUbicacion())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado() != null ? mesa.getEstado().getNombre() : null)
                .build();
    }
    
    /**
     * Convierte Estado entity a EstadoDTO
     */
    private EstadoDTO toEstadoDTO(com.example.demo.entity.Estado estado) {
        if (estado == null) {
            return null;
        }
        
        return EstadoDTO.builder()
                .idEstado(estado.getIdEstado() != null ? estado.getIdEstado().longValue() : null)
                .nombre(estado.getNombre())
                .descripcion(null) // Estado entity no tiene descripción
                .build();
    }
}


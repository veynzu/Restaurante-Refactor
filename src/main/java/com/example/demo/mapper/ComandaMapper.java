package com.example.demo.mapper;

import com.example.demo.dto.response.*;
import com.example.demo.entity.Comanda;
import com.example.demo.entity.DetalleComanda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Comanda Entity y DTOs
 */
@Component
public class ComandaMapper {
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    /**
     * Convierte Comanda entity a ComandaResponseDTO
     */
    public ComandaResponseDTO toResponseDTO(Comanda comanda) {
        if (comanda == null) {
            return null;
        }
        
        return ComandaResponseDTO.builder()
                .idComanda(comanda.getIdComanda() != null ? comanda.getIdComanda().longValue() : null)
                .fecha(comanda.getFecha())
                .mesa(toMesaSimpleDTO(comanda.getMesa()))
                .mesero(usuarioMapper.toSimpleDTO(comanda.getMesero()))
                .cocinero(usuarioMapper.toSimpleDTO(comanda.getCocinero()))
                .estado(toEstadoDTO(comanda.getEstado()))
                .productos(comanda.getDetalleComandas() != null ?
                    comanda.getDetalleComandas().stream()
                        .map(this::toDetalleComandaDTO)
                        .collect(Collectors.toList())
                    : null)
                .total(comanda.calcularTotal())
                .build();
    }
    
    /**
     * Convierte Mesa entity a MesaSimpleDTO
     */
    private MesaSimpleDTO toMesaSimpleDTO(com.example.demo.entity.Mesa mesa) {
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
    
    /**
     * Convierte DetalleComanda entity a DetalleComandaDTO
     */
    private DetalleComandaDTO toDetalleComandaDTO(DetalleComanda detalle) {
        if (detalle == null) {
            return null;
        }
        
        return DetalleComandaDTO.builder()
                .idDetalle(detalle.getIdDetalleComanda() != null ? detalle.getIdDetalleComanda().longValue() : null)
                .nombreProducto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .estado(detalle.getEstado() != null ? detalle.getEstado().getNombre() : null)
                .build();
    }
}


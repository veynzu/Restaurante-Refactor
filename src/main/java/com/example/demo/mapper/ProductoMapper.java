package com.example.demo.mapper;

import com.example.demo.dto.response.ProductoResponseDTO;
import com.example.demo.entity.Producto;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Producto Entity y DTOs
 */
@Component
public class ProductoMapper {
    
    /**
     * Convierte Producto entity a ProductoResponseDTO
     */
    public ProductoResponseDTO toResponseDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto() != null ? producto.getIdProducto().longValue() : null)
                .nombre(producto.getNombre())
                .descripcion(null) // Producto entity no tiene descripción
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .disponible(producto.getStock() != null && producto.getStock() > 0)
                .build();
    }
}


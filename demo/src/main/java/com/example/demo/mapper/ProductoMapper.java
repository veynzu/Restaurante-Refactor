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
        
        System.out.println("🔍 Mapper - Producto ID: " + producto.getIdProducto());
        System.out.println("🔍 Mapper - Categoría es null: " + (producto.getCategoria() == null));
        
        String nombreCategoria = null;
        Integer idCategoria = null;
        
        if (producto.getCategoria() != null) {
            nombreCategoria = producto.getCategoria().getNombre();
            idCategoria = producto.getCategoria().getIdCategoria();
            System.out.println("🔍 Mapper - Categoría ID: " + idCategoria + ", Nombre: " + nombreCategoria);
        } else {
            System.out.println("⚠️ Mapper - Categoría es NULL para producto ID: " + producto.getIdProducto());
        }
        
        ProductoResponseDTO dto = ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto() != null ? producto.getIdProducto().longValue() : null)
                .nombre(producto.getNombre())
                .descripcion(null) // Producto entity no tiene descripción
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(nombreCategoria)
                .idCategoria(idCategoria)
                .disponible(producto.getStock() != null && producto.getStock() > 0)
                .estado(producto.getEstado())
                .build();
        
        System.out.println("🔍 Mapper - DTO creado con categoria: " + dto.getCategoria() + ", idCategoria: " + dto.getIdCategoria());
        
        return dto;
    }
}


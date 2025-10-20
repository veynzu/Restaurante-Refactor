package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de producto")
public class ProductoResponseDTO {
    
    @Schema(description = "ID del producto", example = "1")
    private Long idProducto;
    
    @Schema(description = "Nombre del producto", example = "Hamburguesa Clásica")
    private String nombre;
    
    @Schema(description = "Descripción del producto")
    private String descripcion;
    
    @Schema(description = "Precio del producto", example = "25.00")
    private BigDecimal precio;
    
    @Schema(description = "Stock disponible", example = "50")
    private Integer stock;
    
    @Schema(description = "Categoría del producto", example = "Platos Fuertes")
    private String categoria;
    
    @Schema(description = "Disponibilidad del producto")
    private Boolean disponible;
}


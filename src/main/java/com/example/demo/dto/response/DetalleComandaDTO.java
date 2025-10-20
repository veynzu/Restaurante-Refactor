package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para detalle de comanda
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle de producto en comanda")
public class DetalleComandaDTO {
    
    @Schema(description = "ID del detalle", example = "1")
    private Long idDetalle;
    
    @Schema(description = "Nombre del producto", example = "Hamburguesa Clásica")
    private String nombreProducto;
    
    @Schema(description = "Cantidad", example = "2")
    private Integer cantidad;
    
    @Schema(description = "Precio unitario", example = "25.00")
    private BigDecimal precioUnitario;
    
    @Schema(description = "Subtotal", example = "50.00")
    private BigDecimal subtotal;
    
    @Schema(description = "Estado del detalle", example = "PENDIENTE")
    private String estado;
}


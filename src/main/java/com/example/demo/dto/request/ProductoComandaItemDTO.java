package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para item de producto en comanda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Producto a agregar en la comanda")
public class ProductoComandaItemDTO {
    
    @Schema(description = "ID del producto", example = "1")
    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;
    
    @Schema(description = "Cantidad del producto", example = "2")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
    
    @Schema(description = "Observaciones del producto", example = "Sin cebolla")
    private String observaciones;
}


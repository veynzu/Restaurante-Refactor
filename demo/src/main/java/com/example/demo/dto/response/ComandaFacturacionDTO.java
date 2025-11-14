package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para información de comanda en facturación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de comanda para facturación")
public class ComandaFacturacionDTO {
    
    @Schema(description = "ID de la comanda", example = "1")
    private Integer idComanda;
    
    @Schema(description = "Fecha de la comanda")
    private LocalDateTime fecha;
    
    @Schema(description = "Estado de la comanda", example = "Completado")
    private String estado;
    
    @Schema(description = "Nombre del mesero", example = "Juan Pérez")
    private String mesero;
    
    @Schema(description = "Nombre del cocinero", example = "María García")
    private String cocinero;
    
    @Schema(description = "Total de la comanda", example = "45.50")
    private BigDecimal total;
    
    @Schema(description = "Cantidad de productos en la comanda", example = "3")
    private Integer cantidadProductos;
    
    @Schema(description = "Indica si la comanda está pagada", example = "false")
    private Boolean pagada;
}


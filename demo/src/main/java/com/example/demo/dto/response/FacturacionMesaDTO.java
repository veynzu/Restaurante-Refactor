package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para respuesta de facturación de una mesa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen de facturación de una mesa")
public class FacturacionMesaDTO {
    
    @Schema(description = "ID de la mesa", example = "1")
    private Integer idMesa;
    
    @Schema(description = "Ubicación de la mesa", example = "Terraza 1")
    private String ubicacionMesa;
    
    @Schema(description = "Total de comandas en la mesa", example = "3")
    private Integer totalComandas;
    
    @Schema(description = "Comandas completadas", example = "3")
    private Integer comandasCompletadas;
    
    @Schema(description = "Comandas pendientes o en preparación", example = "0")
    private Integer comandasPendientes;
    
    @Schema(description = "Comandas pagadas", example = "1")
    private Integer comandasPagadas;
    
    @Schema(description = "Indica si todas las comandas están completadas", example = "true")
    private Boolean todasCompletadas;
    
    @Schema(description = "Indica si todas las comandas completadas están pagadas", example = "false")
    private Boolean todasPagadas;
    
    @Schema(description = "Total a pagar (suma de todas las comandas completadas y no pagadas)", example = "125.50")
    private BigDecimal totalAPagar;
    
    @Schema(description = "Lista de comandas con sus totales")
    private List<ComandaFacturacionDTO> comandas;
}


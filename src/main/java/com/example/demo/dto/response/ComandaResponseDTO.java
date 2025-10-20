package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de comanda
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de una comanda")
public class ComandaResponseDTO {
    
    @Schema(description = "ID de la comanda", example = "1")
    private Long idComanda;
    
    @Schema(description = "Fecha y hora de la comanda")
    private LocalDateTime fecha;
    
    @Schema(description = "Información de la mesa")
    private MesaSimpleDTO mesa;
    
    @Schema(description = "Información del mesero")
    private UsuarioSimpleDTO mesero;
    
    @Schema(description = "Información del cocinero")
    private UsuarioSimpleDTO cocinero;
    
    @Schema(description = "Estado de la comanda")
    private EstadoDTO estado;
    
    @Schema(description = "Lista de productos en la comanda")
    private List<DetalleComandaDTO> productos;
    
    @Schema(description = "Total de la comanda", example = "125.50")
    private Double total;
}


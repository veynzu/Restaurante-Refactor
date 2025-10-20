package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de mesa completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de mesa")
public class MesaResponseDTO {
    
    @Schema(description = "ID de la mesa", example = "1")
    private Long idMesa;
    
    @Schema(description = "Ubicación de la mesa", example = "Terraza A1")
    private String ubicacion;
    
    @Schema(description = "Capacidad de la mesa", example = "4")
    private Integer capacidad;
    
    @Schema(description = "Estado de la mesa")
    private EstadoDTO estado;
}


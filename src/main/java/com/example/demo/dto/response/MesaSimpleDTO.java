package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de mesa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información básica de mesa")
public class MesaSimpleDTO {
    
    @Schema(description = "ID de la mesa", example = "1")
    private Long idMesa;
    
    @Schema(description = "Ubicación de la mesa", example = "Terraza A1")
    private String ubicacion;
    
    @Schema(description = "Capacidad de la mesa", example = "4")
    private Integer capacidad;
    
    @Schema(description = "Estado de la mesa", example = "OCUPADO")
    private String estado;
}


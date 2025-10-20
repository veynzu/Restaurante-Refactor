package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de estado")
public class EstadoDTO {
    
    @Schema(description = "ID del estado", example = "1")
    private Long idEstado;
    
    @Schema(description = "Nombre del estado", example = "DISPONIBLE")
    private String nombre;
    
    @Schema(description = "Descripción del estado")
    private String descripcion;
}


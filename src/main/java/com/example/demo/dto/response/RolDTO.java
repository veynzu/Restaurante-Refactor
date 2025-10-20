package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para información de rol
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del rol de usuario")
public class RolDTO {
    
    @Schema(description = "ID del rol", example = "1")
    private Long idRol;
    
    @Schema(description = "Nombre del rol", example = "ADMINISTRADOR")
    private String nombre;
    
    @Schema(description = "Descripción del rol", example = "Administrador del sistema")
    private String descripcion;
}


package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de usuario para referencias
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información básica del usuario")
public class UsuarioSimpleDTO {
    
    @Schema(description = "ID del usuario", example = "mesero001")
    private String idUsuario;
    
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String nombre;
    
    @Schema(description = "Email del usuario", example = "juan@restaurante.com")
    private String email;
    
    @Schema(description = "Rol del usuario", example = "MESERO")
    private String rol;
}


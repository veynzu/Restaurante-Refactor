package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de login exitoso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación exitosa con token JWT")
public class LoginResponseDTO {
    
    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Tipo de token", example = "Bearer")
    private String type;
    
    @Schema(description = "ID del usuario", example = "admin001")
    private String idUsuario;
    
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String nombre;
    
    @Schema(description = "Email del usuario", example = "admin@restaurante.com")
    private String email;
    
    @Schema(description = "Rol del usuario", example = "ADMINISTRADOR")
    private String rol;
    
    @Schema(description = "Tiempo de expiración del token en milisegundos")
    private Long expiresIn;
}


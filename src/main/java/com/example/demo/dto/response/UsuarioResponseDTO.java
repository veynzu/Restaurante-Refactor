package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de usuario (sin password)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa del usuario")
public class UsuarioResponseDTO {
    
    @Schema(description = "ID del usuario", example = "mesero001")
    private String idUsuario;
    
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String nombre;
    
    @Schema(description = "Email del usuario", example = "juan@restaurante.com")
    private String email;
    
    @Schema(description = "Fecha de registro", example = "2025-01-15T10:30:00")
    private LocalDateTime fechaRegistro;
    
    @Schema(description = "Información del rol")
    private RolDTO rol;
    
    @Schema(description = "Lista de teléfonos del usuario")
    private List<String> telefonos;
}


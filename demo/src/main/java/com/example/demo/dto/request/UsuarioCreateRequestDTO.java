package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear un nuevo usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un nuevo usuario")
public class UsuarioCreateRequestDTO {
    
    @Schema(description = "ID único del usuario", example = "mesero001")
    @NotBlank(message = "El ID del usuario es obligatorio")
    @Size(max = 20, message = "El ID no puede exceder 20 caracteres")
    private String idUsuario;
    
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;
    
    @Schema(description = "Email del usuario", example = "juan@restaurante.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    @Schema(description = "Contraseña del usuario", example = "password123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String password;
    
    @Schema(description = "ID del rol del usuario", example = "2")
    @NotNull(message = "El rol es obligatorio")
    private Long idRol;
    
    @Schema(description = "Lista de teléfonos del usuario")
    private List<String> telefonos;
}


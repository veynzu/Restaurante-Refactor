package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una nueva mesa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva mesa")
public class MesaCreateRequestDTO {
    
    @Schema(description = "Ubicación de la mesa", example = "Terraza A1")
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 50, message = "La ubicación no puede exceder 50 caracteres")
    private String ubicacion;
    
    @Schema(description = "Capacidad de la mesa", example = "4")
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1")
    private Integer capacidad;
    
    @Schema(description = "ID del estado inicial", example = "1")
    @NotNull(message = "El estado es obligatorio")
    private Long idEstado;
}


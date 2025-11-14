package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear una nueva comanda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva comanda")
public class ComandaCreateRequestDTO {
    
    @Schema(description = "ID de la mesa", example = "1")
    @NotNull(message = "La mesa es obligatoria")
    private Long idMesa;
    
    @Schema(description = "ID del mesero asignado", example = "mesero001")
    @NotBlank(message = "El mesero es obligatorio")
    private String idMesero;
    
    @Schema(description = "ID del cocinero asignado", example = "cocinero001")
    @NotBlank(message = "El cocinero es obligatorio")
    private String idCocinero;
    
    @Schema(description = "Lista de productos a ordenar")
    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<ProductoComandaItemDTO> productos;
    
    @Schema(description = "Observaciones generales de la comanda")
    private String observaciones;
}


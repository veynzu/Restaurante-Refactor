package com.example.demo.controller;

import com.example.demo.entity.Estado;
import com.example.demo.service.EstadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestión de estados
 * Expone endpoints para operaciones CRUD sobre estados
 */
@Tag(name = "Estados", description = "API para la gestión de estados del sistema (Disponible, Ocupado, Pendiente, etc.)")
@RestController
@RequestMapping("/api/estados")
@CrossOrigin(origins = "*")
public class EstadoController {
    
    @Autowired
    private EstadoService estadoService;
    
    @Operation(
        summary = "Obtener todos los estados",
        description = "Retorna una lista de todos los estados ordenados alfabéticamente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = Estado.class)))
    })
    @GetMapping
    public ResponseEntity<List<Estado>> obtenerTodosLosEstados() {
        try {
            List<Estado> estados = estadoService.obtenerEstadosOrdenados();
            return ResponseEntity.ok(estados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener estado por ID
     * GET /api/estados/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Estado> obtenerEstadoPorId(@PathVariable Integer id) {
        try {
            Optional<Estado> estado = estadoService.obtenerEstadoPorId(id);
            return estado.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener estado por nombre
     * GET /api/estados/nombre/{nombre}
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Estado> obtenerEstadoPorNombre(@PathVariable String nombre) {
        try {
            Optional<Estado> estado = estadoService.obtenerEstadoPorNombre(nombre);
            return estado.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(
        summary = "Crear un nuevo estado",
        description = "Crea un nuevo estado en el sistema. El nombre debe ser único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estado creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado")
    })
    @PostMapping
    public ResponseEntity<?> crearEstado(
        @Parameter(description = "Datos del estado a crear", required = true)
        @Valid @RequestBody Estado estado) {
        try {
            Estado estadoCreado = estadoService.crearEstado(estado);
            return ResponseEntity.status(HttpStatus.CREATED).body(estadoCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar un estado existente
     * PUT /api/estados/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Integer id, @Valid @RequestBody Estado estado) {
        try {
            Estado estadoActualizado = estadoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(estadoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar un estado
     * DELETE /api/estados/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEstado(@PathVariable Integer id) {
        try {
            estadoService.eliminarEstado(id);
            return ResponseEntity.ok(Map.of("message", "Estado eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Buscar estados por texto
     * GET /api/estados/buscar?texto={texto}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Estado>> buscarEstados(@RequestParam String texto) {
        try {
            List<Estado> estados = estadoService.buscarEstadosPorTexto(texto);
            return ResponseEntity.ok(estados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verificar si existe un estado por nombre
     * GET /api/estados/existe/{nombre}
     */
    @GetMapping("/existe/{nombre}")
    public ResponseEntity<Map<String, Boolean>> existeEstadoPorNombre(@PathVariable String nombre) {
        try {
            boolean existe = estadoService.existeEstadoPorNombre(nombre);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de estados
     * GET /api/estados/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarEstados() {
        try {
            long count = estadoService.contarEstados();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

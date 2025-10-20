package com.example.demo.controller;

import com.example.demo.entity.Telefono;
import com.example.demo.service.TelefonoService;
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
 * Controlador REST para la gestión de teléfonos
 * Expone endpoints para operaciones CRUD sobre teléfonos
 */
@Tag(name = "Teléfonos", description = "API para la gestión de números de teléfono de usuarios")
@RestController
@RequestMapping("/api/telefonos")
@CrossOrigin(origins = "*")
public class TelefonoController {
    
    @Autowired
    private TelefonoService telefonoService;
    
    /**
     * Obtener todos los teléfonos
     * GET /api/telefonos
     */
    @GetMapping
    public ResponseEntity<List<Telefono>> obtenerTodosLosTelefonos() {
        try {
            List<Telefono> telefonos = telefonoService.obtenerTodosLosTelefonos();
            return ResponseEntity.ok(telefonos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener teléfono por ID
     * GET /api/telefonos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Telefono> obtenerTelefonoPorId(@PathVariable Integer id) {
        try {
            Optional<Telefono> telefono = telefonoService.obtenerTelefonoPorId(id);
            return telefono.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener teléfono por número
     * GET /api/telefonos/numero/{numero}
     */
    @GetMapping("/numero/{numero}")
    public ResponseEntity<Telefono> obtenerTelefonoPorNumero(@PathVariable String numero) {
        try {
            Optional<Telefono> telefono = telefonoService.obtenerTelefonoPorNumero(numero);
            return telefono.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear un nuevo teléfono
     * POST /api/telefonos
     */
    @PostMapping
    public ResponseEntity<?> crearTelefono(@Valid @RequestBody Telefono telefono) {
        try {
            Telefono telefonoCreado = telefonoService.crearTelefono(telefono);
            return ResponseEntity.status(HttpStatus.CREATED).body(telefonoCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Crear teléfono con número
     * POST /api/telefonos/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearTelefonoConNumero(@RequestBody Map<String, String> request) {
        try {
            String numero = request.get("numero");
            if (numero == null || numero.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El número de teléfono es obligatorio"));
            }
            
            Telefono telefonoCreado = telefonoService.crearTelefonoConNumero(numero);
            return ResponseEntity.status(HttpStatus.CREATED).body(telefonoCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar un teléfono existente
     * PUT /api/telefonos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTelefono(@PathVariable Integer id, @Valid @RequestBody Telefono telefono) {
        try {
            Telefono telefonoActualizado = telefonoService.actualizarTelefono(id, telefono);
            return ResponseEntity.ok(telefonoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar un teléfono
     * DELETE /api/telefonos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTelefono(@PathVariable Integer id) {
        try {
            telefonoService.eliminarTelefono(id);
            return ResponseEntity.ok(Map.of("message", "Teléfono eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Buscar teléfonos por número
     * GET /api/telefonos/buscar?numero={numero}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Telefono>> buscarTelefonos(@RequestParam String numero) {
        try {
            List<Telefono> telefonos = telefonoService.buscarTelefonosPorNumero(numero);
            return ResponseEntity.ok(telefonos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verificar si existe un teléfono por número
     * GET /api/telefonos/existe/{numero}
     */
    @GetMapping("/existe/{numero}")
    public ResponseEntity<Map<String, Boolean>> existeTelefonoPorNumero(@PathVariable String numero) {
        try {
            boolean existe = telefonoService.existeTelefonoPorNumero(numero);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de teléfonos
     * GET /api/telefonos/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarTelefonos() {
        try {
            long count = telefonoService.contarTelefonos();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

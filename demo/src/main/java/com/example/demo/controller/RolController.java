package com.example.demo.controller;

import com.example.demo.entity.Rol;
import com.example.demo.service.RolService;
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
 * Controlador REST para la gestión de roles
 * Expone endpoints para operaciones CRUD sobre roles
 */
@Tag(name = "Roles", description = "API para la gestión de roles de usuario (Administrador, Mesero, Cocinero)")
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolController {
    
    @Autowired
    private RolService rolService;
    
    /**
     * Obtener todos los roles
     * GET /api/roles
     */
    @GetMapping
    public ResponseEntity<List<Rol>> obtenerTodosLosRoles() {
        try {
            List<Rol> roles = rolService.obtenerRolesOrdenados();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener rol por ID
     * GET /api/roles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerRolPorId(@PathVariable Integer id) {
        try {
            Optional<Rol> rol = rolService.obtenerRolPorId(id);
            return rol.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener rol por nombre
     * GET /api/roles/nombre/{nombre}
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Rol> obtenerRolPorNombre(@PathVariable String nombre) {
        try {
            Optional<Rol> rol = rolService.obtenerRolPorNombre(nombre);
            return rol.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear un nuevo rol
     * POST /api/roles
     */
    @PostMapping
    public ResponseEntity<?> crearRol(@Valid @RequestBody Rol rol) {
        try {
            Rol rolCreado = rolService.crearRol(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(rolCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar un rol existente
     * PUT /api/roles/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @Valid @RequestBody Rol rol) {
        try {
            Rol rolActualizado = rolService.actualizarRol(id, rol);
            return ResponseEntity.ok(rolActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar un rol
     * DELETE /api/roles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        try {
            rolService.eliminarRol(id);
            return ResponseEntity.ok(Map.of("message", "Rol eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Buscar roles por texto
     * GET /api/roles/buscar?texto={texto}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Rol>> buscarRoles(@RequestParam String texto) {
        try {
            List<Rol> roles = rolService.buscarRolesPorTexto(texto);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verificar si existe un rol por nombre
     * GET /api/roles/existe/{nombre}
     */
    @GetMapping("/existe/{nombre}")
    public ResponseEntity<Map<String, Boolean>> existeRolPorNombre(@PathVariable String nombre) {
        try {
            boolean existe = rolService.existeRolPorNombre(nombre);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de roles
     * GET /api/roles/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarRoles() {
        try {
            long count = rolService.contarRoles();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear roles básicos del sistema
     * POST /api/roles/basicos
     */
    @PostMapping("/basicos")
    public ResponseEntity<?> crearRolesBasicos() {
        try {
            rolService.crearRolesBasicos();
            return ResponseEntity.ok(Map.of("message", "Roles básicos creados exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}

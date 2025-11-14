package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.entity.UsuarioTelefono;
import com.example.demo.service.UsuarioService;
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
 * Controlador REST para la gestión de usuarios
 * Expone endpoints para operaciones CRUD sobre usuarios
 */
@Tag(name = "Usuarios", description = "API para la gestión de usuarios del sistema (Administradores, Meseros, Cocineros)")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Obtener todos los usuarios
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener usuario por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable String id) {
        try {
            Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
            return usuario.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener usuario por email
     * GET /api/usuarios/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(@PathVariable String email) {
        try {
            Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorEmail(email);
            return usuario.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear un nuevo usuario
     * POST /api/usuarios
     */
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            System.out.println("📥 Recibida petición para crear usuario");
            System.out.println("📋 Usuario recibido: " + usuario.toString());
            System.out.println("📋 ID Usuario: " + usuario.getIdUsuario());
            System.out.println("📋 Nombre: " + usuario.getNombre());
            System.out.println("📋 Email: " + usuario.getEmail());
            System.out.println("📋 Rol: " + (usuario.getRol() != null ? usuario.getRol().toString() : "null"));
            System.out.println("📋 Password: " + (usuario.getPassword() != null ? "***" : "null"));
            
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            System.out.println("✅ Usuario creado exitosamente: " + usuarioCreado.getIdUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error de validación: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Error interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar un usuario existente
     * PUT /api/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String id, @Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar un usuario
     * DELETE /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Obtener usuarios por rol
     * GET /api/usuarios/rol/{idRol}
     */
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<Usuario>> obtenerUsuariosPorRol(@PathVariable Integer idRol) {
        try {
            List<Usuario> usuarios = usuarioService.obtenerUsuariosPorRol(idRol);
            return ResponseEntity.ok(usuarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Buscar usuarios por nombre
     * GET /api/usuarios/buscar?nombre={nombre}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuarios(@RequestParam String nombre) {
        try {
            List<Usuario> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verificar si existe un usuario por email
     * GET /api/usuarios/existe/{email}
     */
    @GetMapping("/existe/{email}")
    public ResponseEntity<Map<String, Boolean>> existeUsuarioPorEmail(@PathVariable String email) {
        try {
            boolean existe = usuarioService.existeUsuarioPorEmail(email);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de usuarios
     * GET /api/usuarios/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarUsuarios() {
        try {
            long count = usuarioService.contarUsuarios();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // NOTA: Login movido a /api/auth/login (AuthController con JWT)
    // El endpoint /api/usuarios/login fue eliminado para evitar duplicación
    
    /**
     * Agregar teléfono a usuario
     * POST /api/usuarios/{id}/telefonos
     */
    @PostMapping("/{id}/telefonos")
    public ResponseEntity<?> agregarTelefonoAUsuario(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String numeroTelefono = request.get("numero");
            if (numeroTelefono == null || numeroTelefono.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El número de teléfono es obligatorio"));
            }
            
            UsuarioTelefono usuarioTelefono = usuarioService.agregarTelefonoAUsuario(id, numeroTelefono);
            return ResponseEntity.ok(usuarioTelefono);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Remover teléfono de usuario
     * DELETE /api/usuarios/{id}/telefonos/{idTelefono}
     */
    @DeleteMapping("/{id}/telefonos/{idTelefono}")
    public ResponseEntity<?> removerTelefonoDeUsuario(@PathVariable String id, @PathVariable Integer idTelefono) {
        try {
            usuarioService.removerTelefonoDeUsuario(id, idTelefono);
            return ResponseEntity.ok(Map.of("message", "Teléfono removido exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}

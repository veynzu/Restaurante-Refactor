package com.example.demo.controller;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.UsuarioCreateRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UsuarioResponseDTO;
import com.example.demo.entity.Rol;
import com.example.demo.entity.Telefono;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.UsuarioTelefono;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.TelefonoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.UsuarioTelefonoRepository;
import com.example.demo.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador para autenticación con JWT
 */
@Tag(name = "Autenticación", description = "Endpoints de autenticación con JWT (Login, Registro)")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private TelefonoRepository telefonoRepository;
    
    @Autowired
    private UsuarioTelefonoRepository usuarioTelefonoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    /**
     * Login con JWT
     */
    @Operation(
        summary = "Login con JWT",
        description = "Autentica un usuario y devuelve un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Buscar usuario por email
            Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            
            if (usuario == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Credenciales inválidas"));
            }
            
            // Verificar contraseña
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Credenciales inválidas"));
            }
            
            // Generar token JWT
            String token = jwtUtil.generateToken(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getRol().getNombre()
            );
            
            // Construir respuesta
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .idUsuario(usuario.getIdUsuario())
                    .nombre(usuario.getNombre())
                    .email(usuario.getEmail())
                    .rol(usuario.getRol().getNombre())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la solicitud: " + e.getMessage()));
        }
    }
    
    /**
     * Registro de nuevo usuario
     */
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioCreateRequestDTO request) {
        try {
            // Verificar si el usuario ya existe
            if (usuarioRepository.existsById(request.getIdUsuario())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El ID de usuario ya existe"));
            }
            
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El email ya está registrado"));
            }
            
            // Buscar rol
            Rol rol = rolRepository.findById(request.getIdRol().intValue())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(request.getIdUsuario());
            usuario.setNombre(request.getNombre());
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRol(rol);
            usuario.setFechaRegistro(LocalDateTime.now());
            
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            
            // Agregar teléfonos si existen
            if (request.getTelefonos() != null && !request.getTelefonos().isEmpty()) {
                for (String numeroTelefono : request.getTelefonos()) {
                    Telefono telefono = new Telefono();
                    telefono.setNumero(numeroTelefono);
                    Telefono telefonoGuardado = telefonoRepository.save(telefono);
                    
                    UsuarioTelefono usuarioTelefono = new UsuarioTelefono();
                    usuarioTelefono.setUsuario(usuarioGuardado);
                    usuarioTelefono.setTelefono(telefonoGuardado);
                    usuarioTelefonoRepository.save(usuarioTelefono);
                }
            }
            
            // Recargar usuario con teléfonos
            usuarioGuardado = usuarioRepository.findById(usuarioGuardado.getIdUsuario()).orElse(usuarioGuardado);
            
            // Convertir a DTO
            UsuarioResponseDTO response = usuarioMapper.toResponseDTO(usuarioGuardado);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }
    
    /**
     * Verificar si el token es válido
     */
    @Operation(
        summary = "Verificar token JWT",
        description = "Valida si un token JWT es válido y no ha expirado"
    )
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);
            
            if (jwtUtil.validateToken(token, userId)) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", userId,
                    "email", jwtUtil.extractEmail(token),
                    "rol", jwtUtil.extractRol(token)
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "error", "Token inválido o expirado"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token inválido: " + e.getMessage()));
        }
    }
}


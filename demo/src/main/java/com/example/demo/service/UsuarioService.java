package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servicio para la gestión de usuarios
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class UsuarioService {
    
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
    
    // Patrón para validar email
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Obtener todos los usuarios
     * @return Lista de todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Obtener usuario por ID
     * @param idUsuario ID del usuario
     * @return Optional<Usuario>
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(String idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }
    
    /**
     * Obtener usuario por email
     * @param email email del usuario
     * @return Optional<Usuario>
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    /**
     * Crear un nuevo usuario
     * @param usuario usuario a crear
     * @return Usuario creado
     * @throws IllegalArgumentException si el usuario ya existe o datos inválidos
     */
    public Usuario crearUsuario(Usuario usuario) {
        // Validar que el rol exista primero
        Rol rol = rolRepository.findById(usuario.getRol().getIdRol())
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + usuario.getRol().getIdRol()));
        
        // Generar ID automáticamente si no se proporciona
        if (usuario.getIdUsuario() == null || usuario.getIdUsuario().trim().isEmpty()) {
            String idGenerado = generarIdUsuario(rol);
            usuario.setIdUsuario(idGenerado);
        }
        
        // Validaciones básicas
        validarDatosUsuario(usuario);
        
        // Validar que el ID de usuario no exista
        if (usuarioRepository.existsById(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("Ya existe un usuario con el ID: " + usuario.getIdUsuario());
        }
        
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        
        // Establecer fecha de registro
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setRol(rol);
        
        // Encriptar contraseña
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Generar un ID único para un usuario basado en su rol
     * @param rol rol del usuario
     * @return ID generado (ej: ADMIN001, MESERO001, COCINERO001)
     */
    private String generarIdUsuario(Rol rol) {
        String prefijo = obtenerPrefijoRol(rol.getNombre());
        
        // Buscar el siguiente número disponible para este rol
        int siguienteNumero = 1;
        String idCandidato;
        
        do {
            idCandidato = String.format("%s%03d", prefijo, siguienteNumero);
            siguienteNumero++;
            
            // Evitar bucle infinito (máximo 999 usuarios por rol)
            if (siguienteNumero > 999) {
                throw new IllegalStateException("Se ha alcanzado el límite de usuarios para el rol: " + rol.getNombre());
            }
        } while (usuarioRepository.existsById(idCandidato));
        
        return idCandidato;
    }
    
    /**
     * Obtener el prefijo del ID basado en el nombre del rol
     * @param nombreRol nombre del rol
     * @return prefijo del ID (ej: ADMIN, MESERO, COCINERO, CAJERO)
     */
    private String obtenerPrefijoRol(String nombreRol) {
        if (nombreRol == null) {
            return "USER";
        }
        
        String nombreUpper = nombreRol.toUpperCase();
        
        // Mapeo de roles a prefijos
        if (nombreUpper.contains("ADMIN") || nombreUpper.equals("ADMINISTRADOR")) {
            return "ADMIN";
        } else if (nombreUpper.contains("MESERO") || nombreUpper.equals("MESERO")) {
            return "MESERO";
        } else if (nombreUpper.contains("COCINERO") || nombreUpper.equals("COCINERO")) {
            return "COCINERO";
        } else if (nombreUpper.contains("CAJERO") || nombreUpper.equals("CAJERO")) {
            return "CAJERO";
        } else {
            // Para roles desconocidos, usar las primeras 6 letras en mayúsculas
            return nombreUpper.length() > 6 ? nombreUpper.substring(0, 6) : nombreUpper;
        }
    }
    
    /**
     * Actualizar un usuario existente
     * @param idUsuario ID del usuario a actualizar
     * @param usuario usuario con los nuevos datos
     * @return Usuario actualizado
     * @throws IllegalArgumentException si el usuario no existe o hay conflictos
     */
    public Usuario actualizarUsuario(String idUsuario, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        
        // Validaciones básicas
        validarDatosUsuario(usuario);
        
        // Validar que el email no exista en otro usuario
        if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && 
            usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        
        // Validar que el rol exista
        Rol rol = rolRepository.findById(usuario.getRol().getIdRol())
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + usuario.getRol().getIdRol()));
        
        // Actualizar datos
        usuarioExistente.setNombre(usuario.getNombre().trim());
        usuarioExistente.setEmail(usuario.getEmail().trim().toLowerCase());
        
        // Actualizar contraseña solo si se proporcionó una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
            usuarioExistente.setPassword(passwordEncriptada);
        }
        
        usuarioExistente.setRol(rol);
        
        return usuarioRepository.save(usuarioExistente);
    }
    
    /**
     * Eliminar un usuario
     * @param idUsuario ID del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe
     * @throws IllegalStateException si el usuario está siendo usado por comandas
     */
    public void eliminarUsuario(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        
        // TODO: Validar que el usuario no esté siendo usado por comandas como mesero o cocinero
        // Por ahora solo eliminamos
        usuarioRepository.delete(usuario);
    }
    
    /**
     * Agregar teléfono a un usuario
     * @param idUsuario ID del usuario
     * @param numeroTelefono número del teléfono
     * @return UsuarioTelefono creado
     */
    public UsuarioTelefono agregarTelefonoAUsuario(String idUsuario, String numeroTelefono) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        
        // Buscar o crear el teléfono
        Telefono telefono = telefonoRepository.findByNumero(numeroTelefono)
            .orElseGet(() -> {
                Telefono nuevoTelefono = new Telefono();
                nuevoTelefono.setNumero(numeroTelefono);
                return telefonoRepository.save(nuevoTelefono);
            });
        
        // Verificar que el usuario no tenga ya este teléfono
        if (usuarioTelefonoRepository.existsByUsuarioAndTelefono(usuario, telefono)) {
            throw new IllegalArgumentException("El usuario ya tiene este número de teléfono");
        }
        
        // Crear la relación
        UsuarioTelefono usuarioTelefono = new UsuarioTelefono(usuario, telefono);
        return usuarioTelefonoRepository.save(usuarioTelefono);
    }
    
    /**
     * Remover teléfono de un usuario
     * @param idUsuario ID del usuario
     * @param idTelefono ID del teléfono
     */
    public void removerTelefonoDeUsuario(String idUsuario, Integer idTelefono) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        
        Telefono telefono = telefonoRepository.findById(idTelefono)
            .orElseThrow(() -> new IllegalArgumentException("Teléfono no encontrado con ID: " + idTelefono));
        
        UsuarioTelefono usuarioTelefono = usuarioTelefonoRepository.findByUsuarioAndTelefono(usuario, telefono)
            .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene este teléfono asociado"));
        
        usuarioTelefonoRepository.delete(usuarioTelefono);
    }
    
    /**
     * Obtener usuarios por rol
     * @param idRol ID del rol
     * @return Lista de usuarios con ese rol
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosPorRol(Integer idRol) {
        Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + idRol));
        
        return usuarioRepository.findByRol(rol);
    }
    
    /**
     * Obtener usuarios por nombre (búsqueda parcial)
     * @param nombre nombre o parte del nombre
     * @return Lista de usuarios que coinciden
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    /**
     * Contar el total de usuarios
     * @return número total de usuarios
     */
    @Transactional(readOnly = true)
    public long contarUsuarios() {
        return usuarioRepository.count();
    }
    
    /**
     * Verificar si existe un usuario con el email dado
     * @param email email del usuario
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    /**
     * Validar los datos básicos de un usuario
     * @param usuario usuario a validar
     * @throws IllegalArgumentException si los datos no son válidos
     */
    private void validarDatosUsuario(Usuario usuario) {
        // El ID ahora es opcional (se genera automáticamente si no se proporciona)
        // Solo validamos que si se proporciona, no esté vacío
        if (usuario.getIdUsuario() != null && usuario.getIdUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario no puede estar vacío si se proporciona");
        }
        
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario no puede estar vacío");
        }
        
        if (!esEmailValido(usuario.getEmail())) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña del usuario no puede estar vacía");
        }
        
        if (usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El usuario debe tener un rol asignado");
        }
    }
    
    /**
     * Validar si el email tiene un formato válido
     * @param email email a validar
     * @return true si es válido, false si no
     */
    private boolean esEmailValido(String email) {
        return PATRON_EMAIL.matcher(email.trim()).matches();
    }
    
    /**
     * Autenticar usuario (login básico)
     * @param email email del usuario
     * @param password contraseña del usuario
     * @return Usuario autenticado
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    @Transactional(readOnly = true)
    public Usuario autenticarUsuario(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        
        // TODO: Implementar encriptación de contraseñas
        if (!usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        return usuario;
    }
}

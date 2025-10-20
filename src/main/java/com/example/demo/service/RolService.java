package com.example.demo.service;

import com.example.demo.entity.Rol;
import com.example.demo.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de roles de usuario
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class RolService {
    
    @Autowired
    private RolRepository rolRepository;
    
    /**
     * Obtener todos los roles
     * @return Lista de todos los roles
     */
    @Transactional(readOnly = true)
    public List<Rol> obtenerTodosLosRoles() {
        return rolRepository.findAll();
    }
    
    /**
     * Obtener rol por ID
     * @param id ID del rol
     * @return Optional<Rol>
     */
    @Transactional(readOnly = true)
    public Optional<Rol> obtenerRolPorId(Integer id) {
        return rolRepository.findById(id);
    }
    
    /**
     * Obtener rol por nombre
     * @param nombre nombre del rol
     * @return Optional<Rol>
     */
    @Transactional(readOnly = true)
    public Optional<Rol> obtenerRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
    
    /**
     * Crear un nuevo rol
     * @param rol rol a crear
     * @return Rol creado
     * @throws IllegalArgumentException si el rol ya existe
     */
    public Rol crearRol(Rol rol) {
        // Validar que el nombre no esté vacío
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        
        // Validar que no exista un rol con el mismo nombre
        if (rolRepository.existsByNombre(rol.getNombre().trim())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + rol.getNombre());
        }
        
        // Normalizar el nombre (trim y capitalizar)
        rol.setNombre(rol.getNombre().trim().toUpperCase());
        
        return rolRepository.save(rol);
    }
    
    /**
     * Actualizar un rol existente
     * @param id ID del rol a actualizar
     * @param rol rol con los nuevos datos
     * @return Rol actualizado
     * @throws IllegalArgumentException si el rol no existe o hay conflictos
     */
    public Rol actualizarRol(Integer id, Rol rol) {
        Rol rolExistente = rolRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        
        // Validar que el nombre no esté vacío
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        
        // Validar que no exista otro rol con el mismo nombre
        String nombreNormalizado = rol.getNombre().trim().toUpperCase();
        if (!rolExistente.getNombre().equals(nombreNormalizado) && 
            rolRepository.existsByNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + nombreNormalizado);
        }
        
        // Actualizar el nombre
        rolExistente.setNombre(nombreNormalizado);
        
        return rolRepository.save(rolExistente);
    }
    
    /**
     * Eliminar un rol
     * @param id ID del rol a eliminar
     * @throws IllegalArgumentException si el rol no existe
     * @throws IllegalStateException si el rol está siendo usado por usuarios
     */
    public void eliminarRol(Integer id) {
        Rol rol = rolRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        
        // TODO: Validar que el rol no esté siendo usado por usuarios
        // Por ahora solo eliminamos
        rolRepository.delete(rol);
    }
    
    /**
     * Verificar si existe un rol con el nombre dado
     * @param nombre nombre del rol
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existeRolPorNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }
    
    /**
     * Buscar roles que contengan el texto dado
     * @param texto texto a buscar
     * @return Lista de roles que coinciden
     */
    @Transactional(readOnly = true)
    public List<Rol> buscarRolesPorTexto(String texto) {
        return rolRepository.findByNombreContaining(texto.toUpperCase());
    }
    
    /**
     * Obtener todos los roles ordenados por nombre
     * @return Lista de roles ordenada alfabéticamente
     */
    @Transactional(readOnly = true)
    public List<Rol> obtenerRolesOrdenados() {
        return rolRepository.findAllByOrderByNombreAsc();
    }
    
    /**
     * Contar el total de roles
     * @return número total de roles
     */
    @Transactional(readOnly = true)
    public long contarRoles() {
        return rolRepository.count();
    }
    
    /**
     * Crear roles básicos del sistema si no existen
     */
    public void crearRolesBasicos() {
        String[] rolesBasicos = {"ADMINISTRADOR", "MESERO", "COCINERO", "CAJERO"};
        
        for (String nombreRol : rolesBasicos) {
            if (!rolRepository.existsByNombre(nombreRol)) {
                Rol rol = new Rol(nombreRol);
                rolRepository.save(rol);
            }
        }
    }
}

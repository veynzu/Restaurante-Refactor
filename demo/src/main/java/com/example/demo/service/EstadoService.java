package com.example.demo.service;

import com.example.demo.entity.Estado;
import com.example.demo.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de estados del sistema
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class EstadoService {
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    /**
     * Obtener todos los estados
     * @return Lista de todos los estados
     */
    @Transactional(readOnly = true)
    public List<Estado> obtenerTodosLosEstados() {
        return estadoRepository.findAll();
    }
    
    /**
     * Obtener estado por ID
     * @param id ID del estado
     * @return Optional<Estado>
     */
    @Transactional(readOnly = true)
    public Optional<Estado> obtenerEstadoPorId(Integer id) {
        return estadoRepository.findById(id);
    }
    
    /**
     * Obtener estado por nombre
     * @param nombre nombre del estado
     * @return Optional<Estado>
     */
    @Transactional(readOnly = true)
    public Optional<Estado> obtenerEstadoPorNombre(String nombre) {
        return estadoRepository.findByNombre(nombre);
    }
    
    /**
     * Crear un nuevo estado
     * @param estado estado a crear
     * @return Estado creado
     * @throws IllegalArgumentException si el estado ya existe
     */
    public Estado crearEstado(Estado estado) {
        // Validar que el nombre no esté vacío
        if (estado.getNombre() == null || estado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado no puede estar vacío");
        }
        
        // Validar que no exista un estado con el mismo nombre
        if (estadoRepository.existsByNombre(estado.getNombre().trim())) {
            throw new IllegalArgumentException("Ya existe un estado con el nombre: " + estado.getNombre());
        }
        
        // Normalizar el nombre (trim y capitalizar)
        estado.setNombre(estado.getNombre().trim().toUpperCase());
        
        return estadoRepository.save(estado);
    }
    
    /**
     * Actualizar un estado existente
     * @param id ID del estado a actualizar
     * @param estado estado con los nuevos datos
     * @return Estado actualizado
     * @throws IllegalArgumentException si el estado no existe o hay conflictos
     */
    public Estado actualizarEstado(Integer id, Estado estado) {
        Estado estadoExistente = estadoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + id));
        
        // Validar que el nombre no esté vacío
        if (estado.getNombre() == null || estado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado no puede estar vacío");
        }
        
        // Validar que no exista otro estado con el mismo nombre
        String nombreNormalizado = estado.getNombre().trim().toUpperCase();
        if (!estadoExistente.getNombre().equals(nombreNormalizado) && 
            estadoRepository.existsByNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe un estado con el nombre: " + nombreNormalizado);
        }
        
        // Actualizar el nombre
        estadoExistente.setNombre(nombreNormalizado);
        
        return estadoRepository.save(estadoExistente);
    }
    
    /**
     * Eliminar un estado
     * @param id ID del estado a eliminar
     * @throws IllegalArgumentException si el estado no existe
     * @throws IllegalStateException si el estado está siendo usado por otras entidades
     */
    public void eliminarEstado(Integer id) {
        Estado estado = estadoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + id));
        
        // TODO: Validar que el estado no esté siendo usado por mesas o comandas
        // Por ahora solo eliminamos
        estadoRepository.delete(estado);
    }
    
    /**
     * Verificar si existe un estado con el nombre dado
     * @param nombre nombre del estado
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existeEstadoPorNombre(String nombre) {
        return estadoRepository.existsByNombre(nombre);
    }
    
    /**
     * Buscar estados que contengan el texto dado
     * @param texto texto a buscar
     * @return Lista de estados que coinciden
     */
    @Transactional(readOnly = true)
    public List<Estado> buscarEstadosPorTexto(String texto) {
        return estadoRepository.findByNombreContaining(texto.toUpperCase());
    }
    
    /**
     * Obtener todos los estados ordenados por nombre
     * @return Lista de estados ordenada alfabéticamente
     */
    @Transactional(readOnly = true)
    public List<Estado> obtenerEstadosOrdenados() {
        return estadoRepository.findAllByOrderByNombreAsc();
    }
    
    /**
     * Contar el total de estados
     * @return número total de estados
     */
    @Transactional(readOnly = true)
    public long contarEstados() {
        return estadoRepository.count();
    }
}

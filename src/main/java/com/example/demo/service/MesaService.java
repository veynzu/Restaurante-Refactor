package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de mesas
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class MesaService {
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    /**
     * Obtener todas las mesas
     * @return Lista de todas las mesas
     */
    @Transactional(readOnly = true)
    public List<Mesa> obtenerTodasLasMesas() {
        return mesaRepository.findAll();
    }
    
    /**
     * Obtener mesa por ID
     * @param id ID de la mesa
     * @return Optional<Mesa>
     */
    @Transactional(readOnly = true)
    public Optional<Mesa> obtenerMesaPorId(Integer id) {
        return mesaRepository.findById(id);
    }
    
    /**
     * Obtener mesas por estado
     * @param idEstado ID del estado
     * @return Lista de mesas con ese estado
     */
    @Transactional(readOnly = true)
    public List<Mesa> obtenerMesasPorEstado(Integer idEstado) {
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        return mesaRepository.findByEstado(estado);
    }
    
    /**
     * Obtener mesas disponibles
     * @return Lista de mesas disponibles
     */
    @Transactional(readOnly = true)
    public List<Mesa> obtenerMesasDisponibles() {
        Estado estadoDisponible = estadoRepository.findByNombre("DISPONIBLE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'DISPONIBLE' no encontrado"));
        
        return mesaRepository.findByEstado(estadoDisponible);
    }
    
    /**
     * Crear una nueva mesa
     * @param mesa mesa a crear
     * @return Mesa creada
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Mesa crearMesa(Mesa mesa) {
        // Validaciones básicas
        validarDatosMesa(mesa);
        
        // Validar que el estado exista
        Estado estado = estadoRepository.findById(mesa.getEstado().getIdEstado())
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + mesa.getEstado().getIdEstado()));
        
        mesa.setEstado(estado);
        
        return mesaRepository.save(mesa);
    }
    
    /**
     * Actualizar una mesa existente
     * @param id ID de la mesa a actualizar
     * @param mesa mesa con los nuevos datos
     * @return Mesa actualizada
     * @throws IllegalArgumentException si la mesa no existe o hay conflictos
     */
    public Mesa actualizarMesa(Integer id, Mesa mesa) {
        Mesa mesaExistente = mesaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + id));
        
        // Validaciones básicas
        validarDatosMesa(mesa);
        
        // Validar que el estado exista
        Estado estado = estadoRepository.findById(mesa.getEstado().getIdEstado())
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + mesa.getEstado().getIdEstado()));
        
        // Actualizar datos
        mesaExistente.setCapacidad(mesa.getCapacidad());
        mesaExistente.setUbicacion(mesa.getUbicacion().trim());
        mesaExistente.setEstado(estado);
        
        return mesaRepository.save(mesaExistente);
    }
    
    /**
     * Eliminar una mesa
     * @param id ID de la mesa a eliminar
     * @throws IllegalArgumentException si la mesa no existe
     * @throws IllegalStateException si la mesa está siendo usada por comandas
     */
    public void eliminarMesa(Integer id) {
        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + id));
        
        // TODO: Validar que la mesa no esté siendo usada por comandas
        // Por ahora solo eliminamos
        mesaRepository.delete(mesa);
    }
    
    /**
     * Cambiar estado de una mesa
     * @param idMesa ID de la mesa
     * @param idEstado ID del nuevo estado
     * @return Mesa actualizada
     */
    public Mesa cambiarEstadoMesa(Integer idMesa, Integer idEstado) {
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa));
        
        Estado nuevoEstado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        mesa.setEstado(nuevoEstado);
        
        return mesaRepository.save(mesa);
    }
    
    /**
     * Ocupar una mesa (cambiar a estado "OCUPADO")
     * @param idMesa ID de la mesa
     * @return Mesa actualizada
     */
    public Mesa ocuparMesa(Integer idMesa) {
        Estado estadoOcupada = estadoRepository.findByNombre("OCUPADO")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'OCUPADO' no encontrado"));
        
        return cambiarEstadoMesa(idMesa, estadoOcupada.getIdEstado());
    }
    
    /**
     * Liberar una mesa (cambiar a estado "DISPONIBLE")
     * @param idMesa ID de la mesa
     * @return Mesa actualizada
     */
    public Mesa liberarMesa(Integer idMesa) {
        Estado estadoDisponible = estadoRepository.findByNombre("DISPONIBLE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'DISPONIBLE' no encontrado"));
        
        return cambiarEstadoMesa(idMesa, estadoDisponible.getIdEstado());
    }
    
    /**
     * Reservar una mesa (cambiar a estado "RESERVADA")
     * @param idMesa ID de la mesa
     * @return Mesa actualizada
     */
    public Mesa reservarMesa(Integer idMesa) {
        Estado estadoReservada = estadoRepository.findByNombre("RESERVADA")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'RESERVADA' no encontrado"));
        
        return cambiarEstadoMesa(idMesa, estadoReservada.getIdEstado());
    }
    
    /**
     * Buscar mesas por ubicación
     * @param ubicacion ubicación a buscar
     * @return Lista de mesas en esa ubicación
     */
    @Transactional(readOnly = true)
    public List<Mesa> buscarMesasPorUbicacion(String ubicacion) {
        return mesaRepository.findByUbicacionContainingIgnoreCase(ubicacion);
    }
    
    /**
     * Buscar mesas por capacidad mínima
     * @param capacidadMinima capacidad mínima requerida
     * @return Lista de mesas con esa capacidad o mayor
     */
    @Transactional(readOnly = true)
    public List<Mesa> buscarMesasPorCapacidadMinima(Integer capacidadMinima) {
        return mesaRepository.findByCapacidadGreaterThanEqual(capacidadMinima);
    }
    
    /**
     * Obtener mesas disponibles por capacidad
     * @param capacidad capacidad requerida
     * @return Lista de mesas disponibles con esa capacidad o mayor
     */
    @Transactional(readOnly = true)
    public List<Mesa> obtenerMesasDisponiblesPorCapacidad(Integer capacidad) {
        Estado estadoDisponible = estadoRepository.findByNombre("DISPONIBLE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'DISPONIBLE' no encontrado"));
        
        return mesaRepository.findByEstadoAndCapacidadGreaterThanEqual(estadoDisponible, capacidad);
    }
    
    /**
     * Contar el total de mesas
     * @return número total de mesas
     */
    @Transactional(readOnly = true)
    public long contarMesas() {
        return mesaRepository.count();
    }
    
    /**
     * Contar mesas por estado
     * @param idEstado ID del estado
     * @return número de mesas con ese estado
     */
    @Transactional(readOnly = true)
    public long contarMesasPorEstado(Integer idEstado) {
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        return mesaRepository.countByEstado(estado);
    }
    
    /**
     * Validar los datos básicos de una mesa
     * @param mesa mesa a validar
     * @throws IllegalArgumentException si los datos no son válidos
     */
    private void validarDatosMesa(Mesa mesa) {
        if (mesa.getCapacidad() == null || mesa.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad de la mesa debe ser mayor a 0");
        }
        
        if (mesa.getCapacidad() > 20) {
            throw new IllegalArgumentException("La capacidad de la mesa no puede exceder 20 personas");
        }
        
        if (mesa.getUbicacion() == null || mesa.getUbicacion().trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación de la mesa no puede estar vacía");
        }
        
        if (mesa.getUbicacion().trim().length() > 50) {
            throw new IllegalArgumentException("La ubicación de la mesa no puede exceder 50 caracteres");
        }
        
        if (mesa.getEstado() == null) {
            throw new IllegalArgumentException("La mesa debe tener un estado asignado");
        }
    }
    
    /**
     * Crear mesa con datos básicos
     * @param capacidad capacidad de la mesa
     * @param ubicacion ubicación de la mesa
     * @param nombreEstado nombre del estado inicial
     * @return Mesa creada
     */
    public Mesa crearMesaConDatos(Integer capacidad, String ubicacion, String nombreEstado) {
        Estado estado = estadoRepository.findByNombre(nombreEstado.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Estado '" + nombreEstado + "' no encontrado"));
        
        Mesa mesa = new Mesa();
        mesa.setCapacidad(capacidad);
        mesa.setUbicacion(ubicacion);
        mesa.setEstado(estado);
        
        return crearMesa(mesa);
    }
}

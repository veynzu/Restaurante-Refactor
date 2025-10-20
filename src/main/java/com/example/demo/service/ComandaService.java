package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de comandas
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class ComandaService {
    
    @Autowired
    private ComandaRepository comandaRepository;
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private DetalleComandaRepository detalleComandaRepository;
    
    /**
     * Obtener todas las comandas
     * @return Lista de todas las comandas
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerTodasLasComandas() {
        return comandaRepository.findAll();
    }
    
    /**
     * Obtener comanda por ID
     * @param id ID de la comanda
     * @return Optional<Comanda>
     */
    @Transactional(readOnly = true)
    public Optional<Comanda> obtenerComandaPorId(Integer id) {
        return comandaRepository.findById(id);
    }
    
    /**
     * Obtener comandas por mesa
     * @param idMesa ID de la mesa
     * @return Lista de comandas de esa mesa
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasPorMesa(Integer idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa));
        
        return comandaRepository.findByMesa(mesa);
    }
    
    /**
     * Obtener comandas por mesero
     * @param idMesero ID del mesero
     * @return Lista de comandas del mesero
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasPorMesero(String idMesero) {
        Usuario mesero = usuarioRepository.findById(idMesero)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idMesero));
        
        return comandaRepository.findByMesero(mesero);
    }
    
    /**
     * Obtener comandas por cocinero
     * @param idCocinero ID del cocinero
     * @return Lista de comandas del cocinero
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasPorCocinero(String idCocinero) {
        Usuario cocinero = usuarioRepository.findById(idCocinero)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idCocinero));
        
        return comandaRepository.findByCocinero(cocinero);
    }
    
    /**
     * Obtener comandas por estado
     * @param idEstado ID del estado
     * @return Lista de comandas con ese estado
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasPorEstado(Integer idEstado) {
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        return comandaRepository.findByEstado(estado);
    }
    
    /**
     * Obtener comandas pendientes
     * @return Lista de comandas pendientes
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasPendientes() {
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no encontrado"));
        
        return comandaRepository.findByEstado(estadoPendiente);
    }
    
    /**
     * Obtener comandas en preparación
     * @return Lista de comandas en preparación
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasEnPreparacion() {
        Estado estadoPreparacion = estadoRepository.findByNombre("PREPARACION")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PREPARACION' no encontrado"));
        
        return comandaRepository.findByEstado(estadoPreparacion);
    }
    
    /**
     * Crear una nueva comanda
     * @param comanda comanda a crear
     * @return Comanda creada
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Comanda crearComanda(Comanda comanda) {
        // Validaciones básicas
        validarDatosComanda(comanda);
        
        // Validar que la mesa exista
        Mesa mesa = mesaRepository.findById(comanda.getMesa().getIdMesa())
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + comanda.getMesa().getIdMesa()));
        
        // Validar que el mesero exista
        Usuario mesero = usuarioRepository.findById(comanda.getMesero().getIdUsuario())
            .orElseThrow(() -> new IllegalArgumentException("Mesero no encontrado con ID: " + comanda.getMesero().getIdUsuario()));
        
        // Validar que el cocinero exista (si se proporciona)
        Usuario cocinero = null;
        if (comanda.getCocinero() != null) {
            cocinero = usuarioRepository.findById(comanda.getCocinero().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Cocinero no encontrado con ID: " + comanda.getCocinero().getIdUsuario()));
        }
        
        // Validar que el estado exista
        Estado estado = estadoRepository.findById(comanda.getEstado().getIdEstado())
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + comanda.getEstado().getIdEstado()));
        
        // Establecer fecha actual si no se proporciona
        if (comanda.getFecha() == null) {
            comanda.setFecha(LocalDateTime.now());
        }
        
        // Asignar las entidades validadas
        comanda.setMesa(mesa);
        comanda.setMesero(mesero);
        comanda.setCocinero(cocinero);
        comanda.setEstado(estado);
        
        return comandaRepository.save(comanda);
    }
    
    /**
     * Actualizar una comanda existente
     * @param id ID de la comanda a actualizar
     * @param comanda comanda con los nuevos datos
     * @return Comanda actualizada
     * @throws IllegalArgumentException si la comanda no existe o hay conflictos
     */
    public Comanda actualizarComanda(Integer id, Comanda comanda) {
        Comanda comandaExistente = comandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + id));
        
        // Validaciones básicas
        validarDatosComanda(comanda);
        
        // Validar que la mesa exista
        Mesa mesa = mesaRepository.findById(comanda.getMesa().getIdMesa())
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + comanda.getMesa().getIdMesa()));
        
        // Validar que el mesero exista
        Usuario mesero = usuarioRepository.findById(comanda.getMesero().getIdUsuario())
            .orElseThrow(() -> new IllegalArgumentException("Mesero no encontrado con ID: " + comanda.getMesero().getIdUsuario()));
        
        // Validar que el cocinero exista (si se proporciona)
        Usuario cocinero = null;
        if (comanda.getCocinero() != null) {
            cocinero = usuarioRepository.findById(comanda.getCocinero().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Cocinero no encontrado con ID: " + comanda.getCocinero().getIdUsuario()));
        }
        
        // Validar que el estado exista
        Estado estado = estadoRepository.findById(comanda.getEstado().getIdEstado())
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + comanda.getEstado().getIdEstado()));
        
        // Actualizar datos
        comandaExistente.setMesa(mesa);
        comandaExistente.setMesero(mesero);
        comandaExistente.setCocinero(cocinero);
        comandaExistente.setEstado(estado);
        comandaExistente.setFecha(comanda.getFecha());
        
        return comandaRepository.save(comandaExistente);
    }
    
    /**
     * Eliminar una comanda
     * @param id ID de la comanda a eliminar
     * @throws IllegalArgumentException si la comanda no existe
     */
    public void eliminarComanda(Integer id) {
        Comanda comanda = comandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + id));
        
        // Eliminar detalles de comanda primero
        List<DetalleComanda> detalles = detalleComandaRepository.findByComanda(comanda);
        detalleComandaRepository.deleteAll(detalles);
        
        // Eliminar la comanda
        comandaRepository.delete(comanda);
    }
    
    /**
     * Cambiar estado de una comanda
     * @param idComanda ID de la comanda
     * @param idEstado ID del nuevo estado
     * @return Comanda actualizada
     */
    public Comanda cambiarEstadoComanda(Integer idComanda, Integer idEstado) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        Estado nuevoEstado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        comanda.setEstado(nuevoEstado);
        
        return comandaRepository.save(comanda);
    }
    
    /**
     * Asignar cocinero a una comanda
     * @param idComanda ID de la comanda
     * @param idCocinero ID del cocinero
     * @return Comanda actualizada
     */
    public Comanda asignarCocinero(Integer idComanda, String idCocinero) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        Usuario cocinero = usuarioRepository.findById(idCocinero)
            .orElseThrow(() -> new IllegalArgumentException("Cocinero no encontrado con ID: " + idCocinero));
        
        comanda.setCocinero(cocinero);
        
        return comandaRepository.save(comanda);
    }
    
    /**
     * Marcar comanda como pendiente
     * @param id ID de la comanda
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoPendiente(Integer id) {
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no encontrado"));
        
        return cambiarEstadoComanda(id, estadoPendiente.getIdEstado());
    }
    
    /**
     * Marcar comanda como en preparación
     * @param id ID de la comanda
     * @param idCocinero ID del cocinero
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoEnPreparacion(Integer id, String idCocinero) {
        Estado estadoPreparacion = estadoRepository.findByNombre("PREPARACION")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PREPARACION' no encontrado"));
        
        Comanda comanda = cambiarEstadoComanda(id, estadoPreparacion.getIdEstado());
        return asignarCocinero(id, idCocinero);
    }
    
    /**
     * Marcar comanda como completada
     * @param id ID de la comanda
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoCompletada(Integer id) {
        Estado estadoCompletada = estadoRepository.findByNombre("COMPLETADO")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'COMPLETADO' no encontrado"));
        
        return cambiarEstadoComanda(id, estadoCompletada.getIdEstado());
    }
    
    /**
     * Marcar comanda como cancelada
     * @param id ID de la comanda
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoCancelada(Integer id) {
        Estado estadoCancelada = estadoRepository.findByNombre("CANCELADO")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'CANCELADO' no encontrado"));
        
        return cambiarEstadoComanda(id, estadoCancelada.getIdEstado());
    }
    
    /**
     * Obtener comandas por rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return Lista de comandas en ese rango
     */
    @Transactional(readOnly = true)
    public List<Comanda> obtenerComandasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return comandaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }
    
    /**
     * Contar el total de comandas
     * @return número total de comandas
     */
    @Transactional(readOnly = true)
    public long contarComandas() {
        return comandaRepository.count();
    }
    
    /**
     * Contar comandas por estado
     * @param idEstado ID del estado
     * @return número de comandas con ese estado
     */
    @Transactional(readOnly = true)
    public long contarComandasPorEstado(Integer idEstado) {
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        return comandaRepository.countByEstado(estado);
    }
    
    /**
     * Obtener total de ventas por rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return total de ventas
     */
    @Transactional(readOnly = true)
    public Double obtenerTotalVentasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Comanda> comandas = obtenerComandasPorRangoFechas(fechaInicio, fechaFin);
        return comandas.stream()
                .mapToDouble(Comanda::calcularTotal)
                .sum();
    }
    
    /**
     * Validar los datos básicos de una comanda
     * @param comanda comanda a validar
     * @throws IllegalArgumentException si los datos no son válidos
     */
    private void validarDatosComanda(Comanda comanda) {
        if (comanda.getMesa() == null) {
            throw new IllegalArgumentException("La comanda debe tener una mesa asignada");
        }
        
        if (comanda.getMesero() == null) {
            throw new IllegalArgumentException("La comanda debe tener un mesero asignado");
        }
        
        if (comanda.getEstado() == null) {
            throw new IllegalArgumentException("La comanda debe tener un estado asignado");
        }
    }
    
    /**
     * Crear comanda con datos básicos
     * @param idMesa ID de la mesa
     * @param idMesero ID del mesero
     * @return Comanda creada
     */
    public Comanda crearComandaConDatos(Integer idMesa, String idMesero) {
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no encontrado"));
        
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa));
        
        Usuario mesero = usuarioRepository.findById(idMesero)
            .orElseThrow(() -> new IllegalArgumentException("Mesero no encontrado con ID: " + idMesero));
        
        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setMesero(mesero);
        comanda.setEstado(estadoPendiente);
        comanda.setFecha(LocalDateTime.now());
        
        return crearComanda(comanda);
    }
}

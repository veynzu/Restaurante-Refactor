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
        // Buscar estado "En Preparacion" - intentar diferentes variaciones
        Estado estadoPreparacion = estadoRepository.findByNombre("En Preparacion")
            .orElse(estadoRepository.findByNombre("EN PREPARACION")
                .orElse(estadoRepository.findByNombre("PREPARACION")
                    .orElse(estadoRepository.findByNombre("en preparacion")
                        .orElseThrow(() -> new IllegalArgumentException("Estado 'En Preparacion' no encontrado. Verifica que exista en la base de datos.")))));
        
        System.out.println("✅ Estado 'En Preparacion' encontrado: " + estadoPreparacion.getNombre() + " (ID: " + estadoPreparacion.getIdEstado() + ")");
        return comandaRepository.findByEstado(estadoPreparacion);
    }
    
    /**
     * Crear una nueva comanda
     * @param comanda comanda a crear
     * @return Comanda creada
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Comanda crearComanda(Comanda comanda) {
        System.out.println("🔍 crearComanda - Iniciando creación de comanda");
        System.out.println("🔍 Comanda recibida - Mesa: " + (comanda.getMesa() != null ? comanda.getMesa().getIdMesa() : "null"));
        System.out.println("🔍 Comanda recibida - Mesero: " + (comanda.getMesero() != null ? comanda.getMesero().getIdUsuario() : "null"));
        System.out.println("🔍 Comanda recibida - Estado: " + (comanda.getEstado() != null ? comanda.getEstado().getIdEstado() + " (" + comanda.getEstado().getNombre() + ")" : "null"));
        
        // Validaciones básicas
        try {
            validarDatosComanda(comanda);
            System.out.println("✅ Validación de datos básicos pasada");
        } catch (Exception e) {
            System.out.println("❌ Error en validación de datos: " + e.getMessage());
            throw e;
        }
        
        // Validar que la mesa exista
        Mesa mesa = null;
        try {
            mesa = mesaRepository.findById(comanda.getMesa().getIdMesa())
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + comanda.getMesa().getIdMesa()));
            System.out.println("✅ Mesa validada: " + mesa.getIdMesa() + " - " + mesa.getUbicacion());
        } catch (Exception e) {
            System.out.println("❌ Error al validar mesa: " + e.getMessage());
            throw e;
        }
        
        // Validar que el mesero exista
        Usuario mesero = null;
        try {
            mesero = usuarioRepository.findById(comanda.getMesero().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Mesero no encontrado con ID: " + comanda.getMesero().getIdUsuario()));
            System.out.println("✅ Mesero validado: " + mesero.getIdUsuario() + " - " + mesero.getNombre());
        } catch (Exception e) {
            System.out.println("❌ Error al validar mesero: " + e.getMessage());
            throw e;
        }
        
        // Validar que el cocinero exista (si se proporciona)
        Usuario cocinero = null;
        if (comanda.getCocinero() != null) {
            try {
                cocinero = usuarioRepository.findById(comanda.getCocinero().getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Cocinero no encontrado con ID: " + comanda.getCocinero().getIdUsuario()));
                System.out.println("✅ Cocinero validado: " + cocinero.getIdUsuario() + " - " + cocinero.getNombre());
            } catch (Exception e) {
                System.out.println("❌ Error al validar cocinero: " + e.getMessage());
                throw e;
            }
        }
        
        // Validar que el estado exista
        Estado estado = null;
        try {
            estado = estadoRepository.findById(comanda.getEstado().getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + comanda.getEstado().getIdEstado()));
            System.out.println("✅ Estado validado: " + estado.getIdEstado() + " - " + estado.getNombre());
        } catch (Exception e) {
            System.out.println("❌ Error al validar estado: " + e.getMessage());
            throw e;
        }
        
        // Establecer fecha actual si no se proporciona
        if (comanda.getFecha() == null) {
            comanda.setFecha(LocalDateTime.now());
            System.out.println("✅ Fecha establecida: " + comanda.getFecha());
        }
        
        // Asignar las entidades validadas
        comanda.setMesa(mesa);
        comanda.setMesero(mesero);
        comanda.setCocinero(cocinero);
        comanda.setEstado(estado);
        
        System.out.println("✅ Asignando entidades a la comanda antes de guardar");
        System.out.println("✅ Comanda lista para guardar - Mesa: " + comanda.getMesa().getIdMesa() + ", Mesero: " + comanda.getMesero().getIdUsuario() + ", Estado: " + comanda.getEstado().getIdEstado());
        
        try {
            Comanda comandaGuardada = comandaRepository.save(comanda);
            System.out.println("✅ Comanda guardada exitosamente con ID: " + comandaGuardada.getIdComanda());
            
            // Cambiar automáticamente la mesa a "Ocupada" si no lo está ya
            try {
                String estadoMesaActual = mesa.getEstado().getNombre();
                if (!"Ocupado".equalsIgnoreCase(estadoMesaActual) && !"Ocupada".equalsIgnoreCase(estadoMesaActual)) {
                    // Buscar estado "Ocupado" - intentar diferentes variaciones
                    Estado estadoOcupado = estadoRepository.findByNombre("Ocupado")
                        .orElse(estadoRepository.findByNombre("OCUPADO")
                            .orElse(estadoRepository.findByNombre("ocupado")
                                .orElse(null)));
                    
                    if (estadoOcupado != null) {
                        mesa.setEstado(estadoOcupado);
                        mesaRepository.save(mesa);
                        System.out.println("✅ Mesa " + mesa.getIdMesa() + " cambiada automáticamente a estado 'Ocupado'");
                    } else {
                        System.out.println("⚠️ No se encontró estado 'Ocupado' para cambiar la mesa automáticamente");
                    }
                } else {
                    System.out.println("ℹ️ Mesa ya está en estado 'Ocupado', no se cambia");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error al cambiar estado de la mesa automáticamente: " + e.getMessage());
                // No lanzamos la excepción, solo registramos el error
                // La comanda ya se guardó correctamente
            }
            
            return comandaGuardada;
        } catch (Exception e) {
            System.out.println("❌ Error al guardar comanda en la base de datos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
        // Buscar estado "Pendiente" - intentar diferentes variaciones
        Estado estadoPendiente = estadoRepository.findByNombre("Pendiente")
            .orElse(estadoRepository.findByNombre("PENDIENTE")
                .orElse(estadoRepository.findByNombre("pendiente")
                    .orElseThrow(() -> new IllegalArgumentException("Estado 'Pendiente' no encontrado. Verifica que exista en la base de datos."))));
        
        System.out.println("✅ Estado 'Pendiente' encontrado: " + estadoPendiente.getNombre() + " (ID: " + estadoPendiente.getIdEstado() + ")");
        return cambiarEstadoComanda(id, estadoPendiente.getIdEstado());
    }
    
    /**
     * Marcar comanda como en preparación
     * @param id ID de la comanda
     * @param idCocinero ID del cocinero
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoEnPreparacion(Integer id, String idCocinero) {
        System.out.println("🔍 marcarComandaComoEnPreparacion - idComanda: " + id + ", idCocinero: " + idCocinero);
        
        // Buscar estado "En Preparacion" - intentar diferentes variaciones
        Estado estadoPreparacion = estadoRepository.findByNombre("En Preparacion")
            .orElse(estadoRepository.findByNombre("EN PREPARACION")
                .orElse(estadoRepository.findByNombre("PREPARACION")
                    .orElse(estadoRepository.findByNombre("en preparacion")
                        .orElseThrow(() -> {
                            System.out.println("❌ No se encontró estado 'En Preparacion' en ninguna variación");
                            return new IllegalArgumentException("Estado 'En Preparacion' no encontrado. Verifica que exista en la base de datos.");
                        }))));
        
        System.out.println("✅ Estado 'En Preparacion' encontrado: " + estadoPreparacion.getNombre() + " (ID: " + estadoPreparacion.getIdEstado() + ")");
        
        Comanda comanda = cambiarEstadoComanda(id, estadoPreparacion.getIdEstado());
        System.out.println("✅ Estado de comanda cambiado a: " + estadoPreparacion.getNombre());
        
        Comanda comandaConCocinero = asignarCocinero(id, idCocinero);
        System.out.println("✅ Cocinero asignado: " + idCocinero);
        
        return comandaConCocinero;
    }
    
    /**
     * Marcar comanda como completada
     * @param id ID de la comanda
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoCompletada(Integer id) {
        // Buscar estado "Completado" - intentar diferentes variaciones
        Estado estadoCompletada = estadoRepository.findByNombre("Completado")
            .orElse(estadoRepository.findByNombre("COMPLETADO")
                .orElse(estadoRepository.findByNombre("completado")
                    .orElseThrow(() -> new IllegalArgumentException("Estado 'Completado' no encontrado. Verifica que exista en la base de datos."))));
        
        System.out.println("✅ Estado 'Completado' encontrado: " + estadoCompletada.getNombre() + " (ID: " + estadoCompletada.getIdEstado() + ")");
        return cambiarEstadoComanda(id, estadoCompletada.getIdEstado());
    }
    
    /**
     * Marcar comanda como cancelada
     * @param id ID de la comanda
     * @return Comanda actualizada
     */
    public Comanda marcarComandaComoCancelada(Integer id) {
        // Buscar estado "Cancelado" - intentar diferentes variaciones
        Estado estadoCancelada = estadoRepository.findByNombre("Cancelado")
            .orElse(estadoRepository.findByNombre("CANCELADO")
                .orElse(estadoRepository.findByNombre("cancelado")
                    .orElseThrow(() -> new IllegalArgumentException("Estado 'Cancelado' no encontrado. Verifica que exista en la base de datos."))));
        
        System.out.println("✅ Estado 'Cancelado' encontrado: " + estadoCancelada.getNombre() + " (ID: " + estadoCancelada.getIdEstado() + ")");
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
     * Verificar si todas las comandas de una mesa están completadas
     * @param idMesa ID de la mesa
     * @return true si todas las comandas están completadas o canceladas, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean verificarTodasComandasCompletadas(Integer idMesa) {
        List<Comanda> comandas = obtenerComandasPorMesa(idMesa);
        
        if (comandas.isEmpty()) {
            return false; // No hay comandas, no se puede facturar
        }
        
        // Verificar que todas las comandas estén completadas o canceladas
        // (excluyendo Pendiente, En Preparacion)
        return comandas.stream()
                .allMatch(c -> {
                    String estadoNombre = c.getEstado().getNombre();
                    return "Completado".equals(estadoNombre) || 
                           "Completada".equals(estadoNombre) ||
                           "Cancelado".equals(estadoNombre) ||
                           "Cancelada".equals(estadoNombre);
                });
    }
    
    /**
     * Finalizar todas las comandas de una mesa (marcarlas como completadas)
     * @param idMesa ID de la mesa
     * @return Número de comandas finalizadas
     */
    @Transactional
    public int finalizarTodasComandasMesa(Integer idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa));
        
        List<Comanda> comandas = obtenerComandasPorMesa(idMesa);
        
        // Buscar estado "Completado" - intentar diferentes variaciones
        Estado estadoCompletado = estadoRepository.findByNombre("Completado")
            .orElse(estadoRepository.findByNombre("COMPLETADO")
                .orElse(estadoRepository.findByNombre("Completada")
                    .orElse(estadoRepository.findByNombre("completado")
                        .orElseThrow(() -> new IllegalArgumentException("Estado 'Completado' no encontrado. Verifica que exista en la base de datos.")))));
        
        // Filtrar solo comandas que no estén completadas o canceladas
        List<Comanda> comandasAFinalizar = comandas.stream()
                .filter(c -> {
                    String estadoNombre = c.getEstado().getNombre();
                    return !"Completado".equals(estadoNombre) && 
                           !"Completada".equals(estadoNombre) &&
                           !"Cancelado".equals(estadoNombre) &&
                           !"Cancelada".equals(estadoNombre);
                })
                .collect(java.util.stream.Collectors.toList());
        
        // Marcar todas como completadas
        int contador = 0;
        for (Comanda comanda : comandasAFinalizar) {
            comanda.setEstado(estadoCompletado);
            comandaRepository.save(comanda);
            contador++;
        }
        
        System.out.println("✅ Finalizadas " + contador + " comanda(s) de la mesa " + idMesa);
        return contador;
    }
    
    /**
     * Obtener resumen de facturación de una mesa
     * Incluye todas las comandas completadas con sus totales
     * @param idMesa ID de la mesa
     * @return FacturacionMesaDTO con el resumen
     */
    @Transactional(readOnly = true)
    public com.example.demo.dto.response.FacturacionMesaDTO obtenerFacturacionMesa(Integer idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa));
        
        List<Comanda> comandas = obtenerComandasPorMesa(idMesa);
        
        // Filtrar solo comandas completadas (no canceladas) y NO pagadas
        List<Comanda> comandasCompletadas = comandas.stream()
                .filter(c -> {
                    String estadoNombre = c.getEstado().getNombre();
                    return ("Completado".equals(estadoNombre) || "Completada".equals(estadoNombre)) 
                           && !c.getPagada(); // Solo comandas no pagadas
                })
                .collect(java.util.stream.Collectors.toList());
        
        // Filtrar comandas pagadas para el conteo
        List<Comanda> comandasPagadas = comandas.stream()
                .filter(c -> c.getPagada() != null && c.getPagada())
                .collect(java.util.stream.Collectors.toList());
        
        // Calcular totales (solo comandas completadas y no pagadas)
        java.math.BigDecimal totalAPagar = comandasCompletadas.stream()
                .map(c -> java.math.BigDecimal.valueOf(c.calcularTotal()))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        // Contar comandas por estado
        long comandasPendientes = comandas.stream()
                .filter(c -> {
                    String estadoNombre = c.getEstado().getNombre();
                    return !"Completado".equals(estadoNombre) && 
                           !"Completada".equals(estadoNombre) &&
                           !"Cancelado".equals(estadoNombre) &&
                           !"Cancelada".equals(estadoNombre);
                })
                .count();
        
        // Mapear comandas a DTOs
        List<com.example.demo.dto.response.ComandaFacturacionDTO> comandasDTO = comandas.stream()
                .map(c -> {
                    // Contar productos en la comanda
                    int cantidadProductos = c.getDetalleComandas() != null ? 
                        c.getDetalleComandas().size() : 0;
                    
                    return com.example.demo.dto.response.ComandaFacturacionDTO.builder()
                            .idComanda(c.getIdComanda())
                            .fecha(c.getFecha())
                            .estado(c.getEstado().getNombre())
                            .mesero(c.getMesero() != null ? c.getMesero().getNombre() : "N/A")
                            .cocinero(c.getCocinero() != null ? c.getCocinero().getNombre() : "N/A")
                            .total(java.math.BigDecimal.valueOf(c.calcularTotal()))
                            .cantidadProductos(cantidadProductos)
                            .pagada(c.getPagada() != null ? c.getPagada() : false)
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());
        
        boolean todasCompletadas = verificarTodasComandasCompletadas(idMesa);
        
        // Verificar si todas las comandas completadas están pagadas
        boolean todasPagadas = comandasCompletadas.isEmpty() || 
            comandasCompletadas.stream().allMatch(c -> c.getPagada() != null && c.getPagada());
        
        return com.example.demo.dto.response.FacturacionMesaDTO.builder()
                .idMesa(mesa.getIdMesa())
                .ubicacionMesa(mesa.getUbicacion())
                .totalComandas(comandas.size())
                .comandasCompletadas(comandasCompletadas.size())
                .comandasPendientes((int) comandasPendientes)
                .comandasPagadas(comandasPagadas.size())
                .todasCompletadas(todasCompletadas)
                .todasPagadas(todasPagadas)
                .totalAPagar(totalAPagar)
                .comandas(comandasDTO)
                .build();
    }
    
    /**
     * Marcar comanda como pagada
     * @param idComanda ID de la comanda
     * @return Comanda actualizada
     */
    @Transactional
    public Comanda marcarComandaComoPagada(Integer idComanda) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        comanda.setPagada(true);
        System.out.println("✅ Comanda " + idComanda + " marcada como pagada");
        return comandaRepository.save(comanda);
    }
    
    /**
     * Marcar todas las comandas completadas de una mesa como pagadas
     * @param idMesa ID de la mesa
     * @return Número de comandas marcadas como pagadas
     */
    @Transactional
    public int marcarTodasComandasPagadas(Integer idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa));
        
        List<Comanda> comandas = obtenerComandasPorMesa(idMesa);
        
        // Filtrar solo comandas completadas y no pagadas
        List<Comanda> comandasAPagar = comandas.stream()
                .filter(c -> {
                    String estadoNombre = c.getEstado().getNombre();
                    return ("Completado".equals(estadoNombre) || "Completada".equals(estadoNombre))
                           && (c.getPagada() == null || !c.getPagada());
                })
                .collect(java.util.stream.Collectors.toList());
        
        // Marcar todas como pagadas
        int contador = 0;
        for (Comanda comanda : comandasAPagar) {
            comanda.setPagada(true);
            comandaRepository.save(comanda);
            contador++;
        }
        
        System.out.println("✅ Marcadas " + contador + " comanda(s) como pagadas para la mesa " + idMesa);
        return contador;
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
        System.out.println("🔍 crearComandaConDatos - idMesa: " + idMesa + ", idMesero: " + idMesero);
        
        // Buscar estado pendiente - intentar diferentes variaciones
        Estado estadoPendiente = estadoRepository.findByNombre("Pendiente")
            .orElse(estadoRepository.findByNombre("PENDIENTE")
                .orElse(estadoRepository.findByNombre("pendiente")
                    .orElseThrow(() -> {
                        System.out.println("❌ No se encontró estado 'Pendiente' en ninguna variación");
                        return new IllegalArgumentException("Estado 'Pendiente' no encontrado. Verifica que exista en la base de datos.");
                    })));
        
        System.out.println("✅ Estado pendiente encontrado: " + estadoPendiente.getNombre() + " (ID: " + estadoPendiente.getIdEstado() + ")");
        
        Mesa mesa = mesaRepository.findById(idMesa)
            .orElseThrow(() -> {
                System.out.println("❌ Mesa no encontrada con ID: " + idMesa);
                return new IllegalArgumentException("Mesa no encontrada con ID: " + idMesa);
            });
        
        System.out.println("✅ Mesa encontrada: " + mesa.getIdMesa() + " - " + mesa.getUbicacion());
        
        Usuario mesero = usuarioRepository.findById(idMesero)
            .orElseThrow(() -> {
                System.out.println("❌ Mesero no encontrado con ID: " + idMesero);
                return new IllegalArgumentException("Mesero no encontrado con ID: " + idMesero);
            });
        
        System.out.println("✅ Mesero encontrado: " + mesero.getIdUsuario() + " - " + mesero.getNombre());
        
        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setMesero(mesero);
        comanda.setEstado(estadoPendiente);
        comanda.setFecha(LocalDateTime.now());
        
        System.out.println("✅ Comanda creada antes de guardar - Mesa: " + comanda.getMesa().getIdMesa() + ", Mesero: " + comanda.getMesero().getIdUsuario());
        
        return crearComanda(comanda);
    }
}

package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de detalles de comanda
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class DetalleComandaService {
    
    @Autowired
    private DetalleComandaRepository detalleComandaRepository;
    
    @Autowired
    private ComandaRepository comandaRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    /**
     * Obtener todos los detalles de comanda
     * @return Lista de todos los detalles
     */
    @Transactional(readOnly = true)
    public List<DetalleComanda> obtenerTodosLosDetalles() {
        return detalleComandaRepository.findAll();
    }
    
    /**
     * Obtener detalle por ID
     * @param id ID del detalle
     * @return Optional<DetalleComanda>
     */
    @Transactional(readOnly = true)
    public Optional<DetalleComanda> obtenerDetallePorId(Integer id) {
        return detalleComandaRepository.findById(id);
    }
    
    /**
     * Obtener detalles por comanda
     * @param idComanda ID de la comanda
     * @return Lista de detalles de esa comanda
     */
    @Transactional(readOnly = true)
    public List<DetalleComanda> obtenerDetallesPorComanda(Integer idComanda) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        return detalleComandaRepository.findByComanda(comanda);
    }
    
    /**
     * Obtener detalles por producto
     * @param idProducto ID del producto
     * @return Lista de detalles de ese producto
     */
    @Transactional(readOnly = true)
    public List<DetalleComanda> obtenerDetallesPorProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));
        
        return detalleComandaRepository.findByProducto(producto);
    }
    
    /**
     * Crear un nuevo detalle de comanda
     * @param detalle detalle a crear
     * @return DetalleComanda creado
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public DetalleComanda crearDetalle(DetalleComanda detalle) {
        // Validaciones básicas
        validarDatosDetalle(detalle);
        
        // Validar que la comanda exista
        Comanda comanda = comandaRepository.findById(detalle.getComanda().getIdComanda())
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + detalle.getComanda().getIdComanda()));
        
        // Validar que el producto exista
        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + detalle.getProducto().getIdProducto()));
        
        // Asignar estado por defecto si no tiene
        if (detalle.getEstado() == null) {
            Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no encontrado"));
            detalle.setEstado(estadoPendiente);
        }
        
        // Validar que el producto esté activo
        if (!producto.getEstado()) {
            throw new IllegalArgumentException("No se puede agregar un producto inactivo a la comanda");
        }
        
        // Validar stock disponible
        if (producto.getStock() < detalle.getCantidad()) {
            throw new IllegalArgumentException("No hay stock suficiente. Stock disponible: " + producto.getStock());
        }
        
        // Verificar si ya existe un detalle con el mismo producto en la misma comanda
        Optional<DetalleComanda> detalleExistente = detalleComandaRepository.findByComandaAndProducto(comanda, producto);
        if (detalleExistente.isPresent()) {
            // Si ya existe, actualizar la cantidad
            DetalleComanda existente = detalleExistente.get();
            int nuevaCantidad = existente.getCantidad() + detalle.getCantidad();
            
            // Validar stock total
            if (producto.getStock() < nuevaCantidad) {
                throw new IllegalArgumentException("No hay stock suficiente para la cantidad total. Stock disponible: " + producto.getStock());
            }
            
            existente.setCantidad(nuevaCantidad);
            existente.calcularSubtotal();
            
            return detalleComandaRepository.save(existente);
        }
        
        // Calcular subtotal
        detalle.calcularSubtotal();
        
        // Asignar las entidades validadas
        detalle.setComanda(comanda);
        detalle.setProducto(producto);
        
        // Reducir stock del producto
        producto.setStock(producto.getStock() - detalle.getCantidad());
        productoRepository.save(producto);
        
        return detalleComandaRepository.save(detalle);
    }
    
    /**
     * Actualizar un detalle existente
     * @param id ID del detalle a actualizar
     * @param detalle detalle con los nuevos datos
     * @return DetalleComanda actualizado
     * @throws IllegalArgumentException si el detalle no existe o hay conflictos
     */
    public DetalleComanda actualizarDetalle(Integer id, DetalleComanda detalle) {
        DetalleComanda detalleExistente = detalleComandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + id));
        
        // Validaciones básicas
        validarDatosDetalle(detalle);
        
        // Validar que la comanda exista
        Comanda comanda = comandaRepository.findById(detalle.getComanda().getIdComanda())
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + detalle.getComanda().getIdComanda()));
        
        // Validar que el producto exista
        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + detalle.getProducto().getIdProducto()));
        
        // Validar que el producto esté activo
        if (!producto.getEstado()) {
            throw new IllegalArgumentException("No se puede usar un producto inactivo");
        }
        
        // Calcular diferencia de stock
        int diferenciaCantidad = detalle.getCantidad() - detalleExistente.getCantidad();
        
        // Validar stock disponible
        if (producto.getStock() < diferenciaCantidad) {
            throw new IllegalArgumentException("No hay stock suficiente. Stock disponible: " + producto.getStock());
        }
        
        // Actualizar stock del producto
        producto.setStock(producto.getStock() - diferenciaCantidad);
        productoRepository.save(producto);
        
        // Actualizar datos del detalle
        detalleExistente.setCantidad(detalle.getCantidad());
        detalleExistente.setComanda(comanda);
        detalleExistente.setProducto(producto);
        detalleExistente.calcularSubtotal();
        
        return detalleComandaRepository.save(detalleExistente);
    }
    
    /**
     * Eliminar un detalle
     * @param id ID del detalle a eliminar
     * @throws IllegalArgumentException si el detalle no existe
     */
    public void eliminarDetalle(Integer id) {
        DetalleComanda detalle = detalleComandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + id));
        
        // Restaurar stock del producto
        Producto producto = detalle.getProducto();
        producto.setStock(producto.getStock() + detalle.getCantidad());
        productoRepository.save(producto);
        
        // Eliminar el detalle
        detalleComandaRepository.delete(detalle);
    }
    
    /**
     * Actualizar cantidad de un detalle
     * @param id ID del detalle
     * @param nuevaCantidad nueva cantidad
     * @return DetalleComanda actualizado
     */
    public DetalleComanda actualizarCantidad(Integer id, Integer nuevaCantidad) {
        DetalleComanda detalle = detalleComandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + id));
        
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        Producto producto = detalle.getProducto();
        int diferenciaCantidad = nuevaCantidad - detalle.getCantidad();
        
        // Validar stock disponible
        if (producto.getStock() < diferenciaCantidad) {
            throw new IllegalArgumentException("No hay stock suficiente. Stock disponible: " + producto.getStock());
        }
        
        // Actualizar stock del producto
        producto.setStock(producto.getStock() - diferenciaCantidad);
        productoRepository.save(producto);
        
        // Actualizar cantidad del detalle
        detalle.setCantidad(nuevaCantidad);
        detalle.calcularSubtotal();
        
        return detalleComandaRepository.save(detalle);
    }
    
    /**
     * Agregar producto a comanda
     * @param idComanda ID de la comanda
     * @param idProducto ID del producto
     * @param cantidad cantidad a agregar
     * @return DetalleComanda creado
     */
    public DetalleComanda agregarProductoAComanda(Integer idComanda, Integer idProducto, Integer cantidad) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));
        
        DetalleComanda detalle = new DetalleComanda();
        detalle.setComanda(comanda);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        
        return crearDetalle(detalle);
    }
    
    /**
     * Obtener total de una comanda
     * @param idComanda ID de la comanda
     * @return total de la comanda
     */
    @Transactional(readOnly = true)
    public Double obtenerTotalComanda(Integer idComanda) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        return comanda.calcularTotal();
    }
    
    /**
     * Obtener detalles por rango de subtotal
     * @param subtotalMinimo subtotal mínimo
     * @param subtotalMaximo subtotal máximo
     * @return Lista de detalles en ese rango
     */
    @Transactional(readOnly = true)
    public List<DetalleComanda> obtenerDetallesPorRangoSubtotal(BigDecimal subtotalMinimo, BigDecimal subtotalMaximo) {
        if (subtotalMinimo.compareTo(subtotalMaximo) > 0) {
            throw new IllegalArgumentException("El subtotal mínimo no puede ser mayor al subtotal máximo");
        }
        
        return detalleComandaRepository.findBySubtotalBetween(subtotalMinimo, subtotalMaximo);
    }
    
    /**
     * Contar el total de detalles
     * @return número total de detalles
     */
    @Transactional(readOnly = true)
    public long contarDetalles() {
        return detalleComandaRepository.count();
    }
    
    /**
     * Contar detalles por comanda
     * @param idComanda ID de la comanda
     * @return número de detalles de esa comanda
     */
    @Transactional(readOnly = true)
    public long contarDetallesPorComanda(Integer idComanda) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        return detalleComandaRepository.countByComanda(comanda);
    }
    
    /**
     * Obtener productos más vendidos
     * @param limite número máximo de productos a retornar
     * @return Lista de productos con sus cantidades vendidas
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerProductosMasVendidos(Integer limite) {
        return detalleComandaRepository.findProductosMasVendidos(limite);
    }
    
    /**
     * Obtener detalles por estado
     * @param idEstado ID del estado
     * @return Lista de detalles con ese estado
     */
    @Transactional(readOnly = true)
    public List<DetalleComanda> obtenerDetallesPorEstado(Integer idEstado) {
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        return detalleComandaRepository.findByEstado(estado);
    }
    
    /**
     * Crear detalle con datos básicos
     * @param idComanda ID de la comanda
     * @param idProducto ID del producto
     * @param cantidad cantidad del producto
     * @param precioUnitario precio unitario del producto
     * @return DetalleComanda creado
     */
    public DetalleComanda crearDetalleConDatos(Integer idComanda, Integer idProducto, Integer cantidad, BigDecimal precioUnitario) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));
        
        // Obtener estado pendiente por defecto
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no encontrado"));
        
        DetalleComanda detalle = new DetalleComanda();
        detalle.setComanda(comanda);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setEstado(estadoPendiente);
        
        return crearDetalle(detalle);
    }
    
    /**
     * Cambiar estado de un detalle
     * @param id ID del detalle
     * @param idEstado ID del nuevo estado
     * @return DetalleComanda actualizado
     */
    public DetalleComanda cambiarEstadoDetalle(Integer id, Integer idEstado) {
        DetalleComanda detalle = detalleComandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + id));
        
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        detalle.setEstado(estado);
        return detalleComandaRepository.save(detalle);
    }
    
    /**
     * Marcar detalle como pendiente
     * @param id ID del detalle
     * @return DetalleComanda actualizado
     */
    public DetalleComanda marcarDetalleComoPendiente(Integer id) {
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PENDIENTE' no encontrado"));
        
        return cambiarEstadoDetalle(id, estadoPendiente.getIdEstado());
    }
    
    /**
     * Marcar detalle como en preparación
     * @param id ID del detalle
     * @return DetalleComanda actualizado
     */
    public DetalleComanda marcarDetalleComoEnPreparacion(Integer id) {
        Estado estadoPreparacion = estadoRepository.findByNombre("PREPARACION")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'PREPARACION' no encontrado"));
        
        return cambiarEstadoDetalle(id, estadoPreparacion.getIdEstado());
    }
    
    /**
     * Marcar detalle como completado
     * @param id ID del detalle
     * @return DetalleComanda actualizado
     */
    public DetalleComanda marcarDetalleComoCompletado(Integer id) {
        Estado estadoCompletado = estadoRepository.findByNombre("COMPLETADO")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'COMPLETADO' no encontrado"));
        
        return cambiarEstadoDetalle(id, estadoCompletado.getIdEstado());
    }
    
    /**
     * Marcar detalle como cancelado
     * @param id ID del detalle
     * @return DetalleComanda actualizado
     */
    public DetalleComanda marcarDetalleComoCancelado(Integer id) {
        Estado estadoCancelado = estadoRepository.findByNombre("CANCELADO")
            .orElseThrow(() -> new IllegalArgumentException("Estado 'CANCELADO' no encontrado"));
        
        return cambiarEstadoDetalle(id, estadoCancelado.getIdEstado());
    }
    
    /**
     * Actualizar precio unitario de un detalle
     * @param id ID del detalle
     * @param nuevoPrecio nuevo precio unitario
     * @return DetalleComanda actualizado
     */
    public DetalleComanda actualizarPrecioUnitario(Integer id, BigDecimal nuevoPrecio) {
        DetalleComanda detalle = detalleComandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + id));
        
        if (nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        
        detalle.setPrecioUnitario(nuevoPrecio);
        detalle.calcularSubtotal();
        
        return detalleComandaRepository.save(detalle);
    }
    
    /**
     * Recalcular subtotal de un detalle
     * @param id ID del detalle
     * @return DetalleComanda actualizado
     */
    public DetalleComanda recalcularSubtotal(Integer id) {
        DetalleComanda detalle = detalleComandaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + id));
        
        detalle.calcularSubtotal();
        
        return detalleComandaRepository.save(detalle);
    }
    
    /**
     * Obtener subtotal de una comanda
     * @param idComanda ID de la comanda
     * @return subtotal de la comanda
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerSubtotalComanda(Integer idComanda) {
        Comanda comanda = comandaRepository.findById(idComanda)
            .orElseThrow(() -> new IllegalArgumentException("Comanda no encontrada con ID: " + idComanda));
        
        List<DetalleComanda> detalles = detalleComandaRepository.findByComanda(comanda);
        
        return detalles.stream()
            .map(DetalleComanda::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Contar detalles por estado
     * @param idEstado ID del estado
     * @return número de detalles con ese estado
     */
    @Transactional(readOnly = true)
    public long contarDetallesPorEstado(Integer idEstado) {
        Estado estado = estadoRepository.findById(idEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + idEstado));
        
        return detalleComandaRepository.countByEstado(estado);
    }
    
    /**
     * Validar los datos básicos de un detalle
     * @param detalle detalle a validar
     * @throws IllegalArgumentException si los datos no son válidos
     */
    private void validarDatosDetalle(DetalleComanda detalle) {
        if (detalle.getComanda() == null) {
            throw new IllegalArgumentException("El detalle debe tener una comanda asignada");
        }
        
        if (detalle.getProducto() == null) {
            throw new IllegalArgumentException("El detalle debe tener un producto asignado");
        }
        
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        if (detalle.getCantidad() > 100) {
            throw new IllegalArgumentException("La cantidad no puede exceder 100");
        }
    }
}

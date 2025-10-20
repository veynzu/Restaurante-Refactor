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
 * Servicio para la gestión de productos
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    /**
     * Obtener todos los productos
     * @return Lista de todos los productos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }
    
    /**
     * Obtener producto por ID
     * @param id ID del producto
     * @return Optional<Producto>
     */
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id);
    }
    
    /**
     * Obtener productos por categoría
     * @param idCategoria ID de la categoría
     * @return Lista de productos de esa categoría
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorCategoria(Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + idCategoria));
        
        return productoRepository.findByCategoria(categoria);
    }
    
    /**
     * Obtener productos activos
     * @return Lista de productos activos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByEstadoTrue();
    }
    
    /**
     * Obtener productos inactivos
     * @return Lista de productos inactivos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosInactivos() {
        return productoRepository.findByEstadoFalse();
    }
    
    /**
     * Crear un nuevo producto
     * @param producto producto a crear
     * @return Producto creado
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Producto crearProducto(Producto producto) {
        // Validaciones básicas
        validarDatosProducto(producto);
        
        // Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getIdCategoria())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + producto.getCategoria().getIdCategoria()));
        
        // Validar que no exista un producto con el mismo nombre
        if (productoRepository.existsByNombre(producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
        }
        
        producto.setCategoria(categoria);
        
        return productoRepository.save(producto);
    }
    
    /**
     * Actualizar un producto existente
     * @param id ID del producto a actualizar
     * @param producto producto con los nuevos datos
     * @return Producto actualizado
     * @throws IllegalArgumentException si el producto no existe o hay conflictos
     */
    public Producto actualizarProducto(Integer id, Producto producto) {
        Producto productoExistente = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        // Validaciones básicas
        validarDatosProducto(producto);
        
        // Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getIdCategoria())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + producto.getCategoria().getIdCategoria()));
        
        // Validar que no exista otro producto con el mismo nombre
        if (!productoExistente.getNombre().equals(producto.getNombre()) && 
            productoRepository.existsByNombre(producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
        }
        
        // Actualizar datos
        productoExistente.setNombre(producto.getNombre().trim());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());
        productoExistente.setEstado(producto.getEstado());
        productoExistente.setCategoria(categoria);
        
        return productoRepository.save(productoExistente);
    }
    
    /**
     * Eliminar un producto
     * @param id ID del producto a eliminar
     * @throws IllegalArgumentException si el producto no existe
     * @throws IllegalStateException si el producto está siendo usado por comandas
     */
    public void eliminarProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        // TODO: Validar que el producto no esté siendo usado por detalles de comanda
        // Por ahora solo eliminamos
        productoRepository.delete(producto);
    }
    
    /**
     * Activar un producto
     * @param id ID del producto
     * @return Producto actualizado
     */
    public Producto activarProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        producto.setEstado(true);
        
        return productoRepository.save(producto);
    }
    
    /**
     * Desactivar un producto
     * @param id ID del producto
     * @return Producto actualizado
     */
    public Producto desactivarProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        producto.setEstado(false);
        
        return productoRepository.save(producto);
    }
    
    /**
     * Actualizar stock de un producto
     * @param id ID del producto
     * @param nuevoStock nuevo stock
     * @return Producto actualizado
     */
    public Producto actualizarStock(Integer id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        producto.setStock(nuevoStock);
        
        return productoRepository.save(producto);
    }
    
    /**
     * Reducir stock de un producto
     * @param id ID del producto
     * @param cantidad cantidad a reducir
     * @return Producto actualizado
     * @throws IllegalArgumentException si no hay stock suficiente
     */
    public Producto reducirStock(Integer id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a 0");
        }
        
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay stock suficiente. Stock actual: " + producto.getStock());
        }
        
        producto.setStock(producto.getStock() - cantidad);
        
        return productoRepository.save(producto);
    }
    
    /**
     * Aumentar stock de un producto
     * @param id ID del producto
     * @param cantidad cantidad a aumentar
     * @return Producto actualizado
     */
    public Producto aumentarStock(Integer id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor a 0");
        }
        
        producto.setStock(producto.getStock() + cantidad);
        
        return productoRepository.save(producto);
    }
    
    /**
     * Buscar productos por nombre
     * @param nombre nombre o parte del nombre
     * @return Lista de productos que coinciden
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    /**
     * Buscar productos por rango de precio
     * @param precioMinimo precio mínimo
     * @param precioMaximo precio máximo
     * @return Lista de productos en ese rango
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorRangoPrecio(BigDecimal precioMinimo, BigDecimal precioMaximo) {
        if (precioMinimo.compareTo(precioMaximo) > 0) {
            throw new IllegalArgumentException("El precio mínimo no puede ser mayor al precio máximo");
        }
        
        return productoRepository.findByPrecioBetween(precioMinimo, precioMaximo);
    }
    
    /**
     * Obtener productos con stock bajo
     * @param stockMinimo stock mínimo
     * @return Lista de productos con stock menor al mínimo
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosConStockBajo(Integer stockMinimo) {
        return productoRepository.findByStockLessThan(stockMinimo);
    }
    
    /**
     * Contar el total de productos
     * @return número total de productos
     */
    @Transactional(readOnly = true)
    public long contarProductos() {
        return productoRepository.count();
    }
    
    /**
     * Contar productos por categoría
     * @param idCategoria ID de la categoría
     * @return número de productos de esa categoría
     */
    @Transactional(readOnly = true)
    public long contarProductosPorCategoria(Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + idCategoria));
        
        return productoRepository.countByCategoria(categoria);
    }
    
    /**
     * Verificar si existe un producto con el nombre dado
     * @param nombre nombre del producto
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existeProductoPorNombre(String nombre) {
        return productoRepository.existsByNombre(nombre);
    }
    
    /**
     * Validar los datos básicos de un producto
     * @param producto producto a validar
     * @throws IllegalArgumentException si los datos no son válidos
     */
    private void validarDatosProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        
        if (producto.getNombre().trim().length() > 50) {
            throw new IllegalArgumentException("El nombre del producto no puede exceder 50 caracteres");
        }
        
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a 0");
        }
        
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock del producto no puede ser negativo");
        }
        
        if (producto.getCategoria() == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría asignada");
        }
    }
    
    /**
     * Crear producto con datos básicos
     * @param nombre nombre del producto
     * @param precio precio del producto
     * @param stock stock inicial
     * @param idCategoria ID de la categoría
     * @return Producto creado
     */
    public Producto crearProductoConDatos(String nombre, BigDecimal precio, Integer stock, Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + idCategoria));
        
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setEstado(true); // Por defecto activo
        producto.setCategoria(categoria);
        
        return crearProducto(producto);
    }
}

package com.example.demo.repository;

import com.example.demo.entity.Categoria;
import com.example.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Producto
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    /**
     * Buscar producto por nombre
     * @param nombre nombre del producto
     * @return Optional<Producto>
     */
    Optional<Producto> findByNombre(String nombre);
    
    /**
     * Verificar si existe un producto con el nombre dado
     * @param nombre nombre del producto
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Buscar productos por categoría
     * @param categoria categoría del producto
     * @return List<Producto>
     */
    List<Producto> findByCategoria(Categoria categoria);
    
    /**
     * Buscar productos por nombre de categoría
     * @param nombreCategoria nombre de la categoría
     * @return List<Producto>
     */
    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = ?1 AND p.estado = true ORDER BY p.nombre")
    List<Producto> findByNombreCategoria(String nombreCategoria);
    
    /**
     * Obtener productos activos
     * @return List<Producto>
     */
    List<Producto> findByEstadoTrue();
    
    /**
     * Obtener productos inactivos
     * @return List<Producto>
     */
    List<Producto> findByEstadoFalse();
    
    /**
     * Buscar productos que contengan el texto dado en el nombre (búsqueda parcial)
     * @param nombre nombre o parte del nombre
     * @return List<Producto>
     */
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %?1% AND p.estado = true ORDER BY p.nombre")
    List<Producto> findByNombreContainingAndActivos(String nombre);
    
    /**
     * Buscar productos que contengan el texto dado en el nombre (ignorando mayúsculas)
     * @param nombre nombre o parte del nombre
     * @return List<Producto>
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Buscar productos por rango de precio
     * @param precioMinimo precio mínimo
     * @param precioMaximo precio máximo
     * @return List<Producto>
     */
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN ?1 AND ?2 AND p.estado = true ORDER BY p.precio")
    List<Producto> findByRangoPrecio(BigDecimal precioMinimo, BigDecimal precioMaximo);
    
    /**
     * Buscar productos por rango de precio (método alternativo)
     * @param precioMinimo precio mínimo
     * @param precioMaximo precio máximo
     * @return List<Producto>
     */
    List<Producto> findByPrecioBetween(BigDecimal precioMinimo, BigDecimal precioMaximo);
    
    /**
     * Obtener productos con stock bajo (menor a la cantidad especificada)
     * @param cantidadMinima cantidad mínima de stock
     * @return List<Producto>
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= ?1 AND p.estado = true ORDER BY p.stock")
    List<Producto> findProductosConStockBajo(Integer cantidadMinima);
    
    /**
     * Buscar productos con stock menor que el indicado
     * @param stock stock máximo
     * @return List<Producto>
     */
    List<Producto> findByStockLessThan(Integer stock);
    
    /**
     * Obtener productos disponibles (con stock > 0 y activos)
     * @return List<Producto>
     */
    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.estado = true ORDER BY p.nombre")
    List<Producto> findProductosDisponibles();
    
    /**
     * Obtener todos los productos con categoría cargada (JOIN FETCH)
     * @return List<Producto>
     */
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.categoria")
    List<Producto> findAllWithCategoria();
    
    /**
     * Obtener todos los productos ordenados por nombre
     * @return List<Producto>
     */
    List<Producto> findAllByOrderByNombreAsc();
    
    /**
     * Obtener productos ordenados por precio
     * @return List<Producto>
     */
    List<Producto> findAllByOrderByPrecioAsc();
    
    /**
     * Buscar productos por categoría y estado
     * @param categoria categoría del producto
     * @param estado estado del producto
     * @return List<Producto>
     */
    List<Producto> findByCategoriaAndEstado(Categoria categoria, Boolean estado);
    
    /**
     * Contar productos por categoría
     * @param categoria categoría del producto
     * @return número de productos
     */
    long countByCategoria(Categoria categoria);
    
    /**
     * Contar productos activos
     * @return número de productos activos
     */
    long countByEstadoTrue();
}

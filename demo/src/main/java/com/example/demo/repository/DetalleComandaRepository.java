package com.example.demo.repository;

import com.example.demo.entity.Comanda;
import com.example.demo.entity.DetalleComanda;
import com.example.demo.entity.Estado;
import com.example.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad DetalleComanda
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface DetalleComandaRepository extends JpaRepository<DetalleComanda, Integer> {
    
    /**
     * Buscar detalles por comanda
     * @param comanda comanda del detalle
     * @return List<DetalleComanda>
     */
    List<DetalleComanda> findByComanda(Comanda comanda);
    
    /**
     * Buscar detalles por ID de comanda
     * @param idComanda ID de la comanda
     * @return List<DetalleComanda>
     */
    @Query("SELECT d FROM DetalleComanda d WHERE d.comanda.idComanda = ?1 ORDER BY d.idDetalleComanda")
    List<DetalleComanda> findByComandaId(Integer idComanda);
    
    /**
     * Buscar detalles por producto
     * @param producto producto del detalle
     * @return List<DetalleComanda>
     */
    List<DetalleComanda> findByProducto(Producto producto);
    
    /**
     * Buscar detalles por ID de producto
     * @param idProducto ID del producto
     * @return List<DetalleComanda>
     */
    @Query("SELECT d FROM DetalleComanda d WHERE d.producto.idProducto = ?1 ORDER BY d.comanda.fecha DESC")
    List<DetalleComanda> findByProductoId(Integer idProducto);
    
    /**
     * Buscar detalles por comanda y producto
     * @param comanda comanda del detalle
     * @param producto producto del detalle
     * @return Optional<DetalleComanda>
     */
    Optional<DetalleComanda> findByComandaAndProducto(Comanda comanda, Producto producto);
    
    /**
     * Buscar detalles por estado
     * @param estado estado del detalle
     * @return List<DetalleComanda>
     */
    List<DetalleComanda> findByEstado(Estado estado);
    
    /**
     * Buscar detalles por rango de subtotal
     * @param subtotalMinimo subtotal mínimo
     * @param subtotalMaximo subtotal máximo
     * @return List<DetalleComanda>
     */
    List<DetalleComanda> findBySubtotalBetween(BigDecimal subtotalMinimo, BigDecimal subtotalMaximo);
    
    /**
     * Contar detalles por estado
     * @param estado estado del detalle
     * @return número de detalles
     */
    long countByEstado(Estado estado);
    
    /**
     * Obtener todos los detalles ordenados por ID de comanda
     * @return List<DetalleComanda>
     */
    @Query("SELECT d FROM DetalleComanda d ORDER BY d.comanda.idComanda, d.idDetalleComanda")
    List<DetalleComanda> findAllOrderByComanda();
    
    /**
     * Contar detalles por comanda
     * @param comanda comanda del detalle
     * @return número de detalles
     */
    long countByComanda(Comanda comanda);
    
    /**
     * Contar detalles por producto
     * @param producto producto del detalle
     * @return número de detalles
     */
    long countByProducto(Producto producto);
    
    /**
     * Buscar productos más vendidos (por cantidad total)
     * @param limite número máximo de resultados
     * @return List<Object[]> con [producto, cantidadTotal]
     */
    @Query("SELECT d.producto, SUM(d.cantidad) as cantidadTotal " +
           "FROM DetalleComanda d " +
           "GROUP BY d.producto " +
           "ORDER BY cantidadTotal DESC")
    List<Object[]> findProductosMasVendidos(@Param("limite") Integer limite);
    
    /**
     * Buscar productos más vendidos en un rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @param limite número máximo de resultados
     * @return List<Object[]> con [producto, cantidadTotal]
     */
    @Query("SELECT d.producto, SUM(d.cantidad) as cantidadTotal " +
           "FROM DetalleComanda d " +
           "WHERE d.comanda.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY d.producto " +
           "ORDER BY cantidadTotal DESC")
    List<Object[]> findProductosMasVendidosPorFecha(@Param("fechaInicio") String fechaInicio, 
                                                   @Param("fechaFin") String fechaFin, 
                                                   @Param("limite") Integer limite);
}

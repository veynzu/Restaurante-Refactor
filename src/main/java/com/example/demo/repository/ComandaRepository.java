package com.example.demo.repository;

import com.example.demo.entity.Comanda;
import com.example.demo.entity.Estado;
import com.example.demo.entity.Mesa;
import com.example.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Comanda
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Integer> {
    
    /**
     * Buscar comandas por mesa
     * @param mesa mesa de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByMesa(Mesa mesa);
    
    /**
     * Buscar comandas por ID de mesa
     * @param idMesa ID de la mesa
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.mesa.idMesa = ?1 ORDER BY c.fecha DESC")
    List<Comanda> findByMesaId(Integer idMesa);
    
    /**
     * Buscar comandas por mesero
     * @param mesero mesero de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByMesero(Usuario mesero);
    
    /**
     * Buscar comandas por cocinero
     * @param cocinero cocinero de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByCocinero(Usuario cocinero);
    
    /**
     * Buscar comandas por estado
     * @param estado estado de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByEstado(Estado estado);
    
    /**
     * Buscar comandas por nombre de estado
     * @param nombreEstado nombre del estado
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.estado.nombre = ?1 ORDER BY c.fecha DESC")
    List<Comanda> findByNombreEstado(String nombreEstado);
    
    /**
     * Obtener comandas pendientes
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.estado.nombre = 'Pendiente' ORDER BY c.fecha ASC")
    List<Comanda> findComandasPendientes();
    
    /**
     * Obtener comandas en preparación
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.estado.nombre = 'En Preparación' ORDER BY c.fecha ASC")
    List<Comanda> findComandasEnPreparacion();
    
    /**
     * Obtener comandas completadas
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.estado.nombre = 'Completada' ORDER BY c.fecha DESC")
    List<Comanda> findComandasCompletadas();
    
    /**
     * Buscar comandas por rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.fecha BETWEEN ?1 AND ?2 ORDER BY c.fecha DESC")
    List<Comanda> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Buscar comandas por rango de fechas (método alternativo)
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return List<Comanda>
     */
    List<Comanda> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Buscar comandas del día actual
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE DATE(c.fecha) = CURRENT_DATE ORDER BY c.fecha DESC")
    List<Comanda> findComandasDelDia();
    
    /**
     * Buscar comandas por mesero y estado
     * @param mesero mesero de la comanda
     * @param estado estado de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByMeseroAndEstado(Usuario mesero, Estado estado);
    
    /**
     * Buscar comandas por cocinero y estado
     * @param cocinero cocinero de la comanda
     * @param estado estado de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByCocineroAndEstado(Usuario cocinero, Estado estado);
    
    /**
     * Obtener comandas activas (no completadas ni canceladas)
     * @return List<Comanda>
     */
    @Query("SELECT c FROM Comanda c WHERE c.estado.nombre NOT IN ('Completada', 'Cancelada') ORDER BY c.fecha ASC")
    List<Comanda> findComandasActivas();
    
    /**
     * Contar comandas por estado
     * @param estado estado de la comanda
     * @return número de comandas
     */
    long countByEstado(Estado estado);
    
    /**
     * Contar comandas pendientes
     * @return número de comandas pendientes
     */
    @Query("SELECT COUNT(c) FROM Comanda c WHERE c.estado.nombre = 'Pendiente'")
    long countComandasPendientes();
    
    /**
     * Buscar comandas por mesa y estado
     * @param mesa mesa de la comanda
     * @param estado estado de la comanda
     * @return List<Comanda>
     */
    List<Comanda> findByMesaAndEstado(Mesa mesa, Estado estado);
}

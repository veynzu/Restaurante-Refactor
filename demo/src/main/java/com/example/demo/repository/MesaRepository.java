package com.example.demo.repository;

import com.example.demo.entity.Estado;
import com.example.demo.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Mesa
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    
    /**
     * Buscar mesas por estado
     * @param estado estado de la mesa
     * @return List<Mesa>
     */
    List<Mesa> findByEstado(Estado estado);
    
    /**
     * Buscar mesas por nombre de estado
     * @param nombreEstado nombre del estado
     * @return List<Mesa>
     */
    @Query("SELECT m FROM Mesa m WHERE m.estado.nombre = ?1")
    List<Mesa> findByNombreEstado(String nombreEstado);
    
    /**
     * Obtener mesas disponibles
     * @return List<Mesa>
     */
    @Query("SELECT m FROM Mesa m WHERE m.estado.nombre = 'Disponible' ORDER BY m.idMesa")
    List<Mesa> findMesasDisponibles();
    
    /**
     * Obtener mesas ocupadas
     * @return List<Mesa>
     */
    @Query("SELECT m FROM Mesa m WHERE m.estado.nombre = 'Ocupada' ORDER BY m.idMesa")
    List<Mesa> findMesasOcupadas();
    
    /**
     * Obtener mesas reservadas
     * @return List<Mesa>
     */
    @Query("SELECT m FROM Mesa m WHERE m.estado.nombre = 'Reservada' ORDER BY m.idMesa")
    List<Mesa> findMesasReservadas();
    
    /**
     * Buscar mesas por capacidad mínima
     * @param capacidad capacidad mínima requerida
     * @return List<Mesa>
     */
    @Query("SELECT m FROM Mesa m WHERE m.capacidad >= ?1 AND m.estado.nombre = 'Disponible' ORDER BY m.capacidad")
    List<Mesa> findMesasDisponiblesPorCapacidad(Integer capacidad);
    
    /**
     * Buscar mesas por ubicación
     * @param ubicacion ubicación de la mesa
     * @return List<Mesa>
     */
    List<Mesa> findByUbicacion(String ubicacion);
    
    /**
     * Buscar mesas que contengan el texto dado en la ubicación (búsqueda parcial)
     * @param ubicacion ubicación o parte de la ubicación
     * @return List<Mesa>
     */
    @Query("SELECT m FROM Mesa m WHERE m.ubicacion LIKE %?1%")
    List<Mesa> findByUbicacionContaining(String ubicacion);
    
    /**
     * Buscar mesas que contengan el texto dado en la ubicación (ignorando mayúsculas)
     * @param ubicacion ubicación o parte de la ubicación
     * @return List<Mesa>
     */
    List<Mesa> findByUbicacionContainingIgnoreCase(String ubicacion);
    
    /**
     * Buscar mesas por capacidad mínima
     * @param capacidad capacidad mínima
     * @return List<Mesa>
     */
    List<Mesa> findByCapacidadGreaterThanEqual(Integer capacidad);
    
    /**
     * Buscar mesas disponibles por capacidad mínima
     * @param estado estado de la mesa
     * @param capacidad capacidad mínima
     * @return List<Mesa>
     */
    List<Mesa> findByEstadoAndCapacidadGreaterThanEqual(Estado estado, Integer capacidad);
    
    /**
     * Obtener todas las mesas ordenadas por ID
     * @return List<Mesa>
     */
    List<Mesa> findAllByOrderByIdMesaAsc();
    
    /**
     * Obtener todas las mesas ordenadas por capacidad
     * @return List<Mesa>
     */
    List<Mesa> findAllByOrderByCapacidadAsc();
    
    /**
     * Contar mesas por estado
     * @param estado estado de la mesa
     * @return número de mesas
     */
    long countByEstado(Estado estado);
    
    /**
     * Contar mesas disponibles
     * @return número de mesas disponibles
     */
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.estado.nombre = 'Disponible'")
    long countMesasDisponibles();
}

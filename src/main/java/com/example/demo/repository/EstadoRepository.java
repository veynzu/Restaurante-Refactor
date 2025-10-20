package com.example.demo.repository;

import com.example.demo.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Estado
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    
    /**
     * Buscar estado por nombre
     * @param nombre nombre del estado
     * @return Optional<Estado>
     */
    Optional<Estado> findByNombre(String nombre);
    
    /**
     * Verificar si existe un estado con el nombre dado
     * @param nombre nombre del estado
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtener todos los estados ordenados por nombre
     * @return List<Estado>
     */
    List<Estado> findAllByOrderByNombreAsc();
    
    /**
     * Buscar estados que contengan el texto dado (búsqueda parcial)
     * @param nombre nombre o parte del nombre
     * @return List<Estado>
     */
    @Query("SELECT e FROM Estado e WHERE e.nombre LIKE %?1%")
    List<Estado> findByNombreContaining(String nombre);
}

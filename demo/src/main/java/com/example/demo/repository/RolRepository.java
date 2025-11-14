package com.example.demo.repository;

import com.example.demo.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Rol
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    /**
     * Buscar rol por nombre
     * @param nombre nombre del rol
     * @return Optional<Rol>
     */
    Optional<Rol> findByNombre(String nombre);
    
    /**
     * Verificar si existe un rol con el nombre dado
     * @param nombre nombre del rol
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtener todos los roles ordenados por nombre
     * @return List<Rol>
     */
    List<Rol> findAllByOrderByNombreAsc();
    
    /**
     * Buscar roles que contengan el texto dado (búsqueda parcial)
     * @param nombre nombre o parte del nombre
     * @return List<Rol>
     */
    @Query("SELECT r FROM Rol r WHERE r.nombre LIKE %?1%")
    List<Rol> findByNombreContaining(String nombre);
}

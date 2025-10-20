package com.example.demo.repository;

import com.example.demo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Categoria
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    /**
     * Buscar categoría por nombre
     * @param nombre nombre de la categoría
     * @return Optional<Categoria>
     */
    Optional<Categoria> findByNombre(String nombre);
    
    /**
     * Verificar si existe una categoría con el nombre dado
     * @param nombre nombre de la categoría
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtener todas las categorías ordenadas por nombre
     * @return List<Categoria>
     */
    List<Categoria> findAllByOrderByNombreAsc();
    
    /**
     * Buscar categorías que contengan el texto dado (búsqueda parcial)
     * @param nombre nombre o parte del nombre
     * @return List<Categoria>
     */
    @Query("SELECT c FROM Categoria c WHERE c.nombre LIKE %?1%")
    List<Categoria> findByNombreContaining(String nombre);
    
    /**
     * Obtener categorías con productos activos
     * @return List<Categoria>
     */
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.productos p WHERE p.estado = true")
    List<Categoria> findCategoriasConProductosActivos();
}

package com.example.demo.repository;

import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    /**
     * Buscar usuario por email
     * @param email email del usuario
     * @return Optional<Usuario>
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verificar si existe un usuario con el email dado
     * @param email email del usuario
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
    
    /**
     * Buscar usuarios por rol
     * @param rol rol del usuario
     * @return List<Usuario>
     */
    List<Usuario> findByRol(Rol rol);
    
    /**
     * Buscar usuarios por nombre de rol
     * @param nombreRol nombre del rol
     * @return List<Usuario>
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = ?1")
    List<Usuario> findByNombreRol(String nombreRol);
    
    /**
     * Buscar usuarios que contengan el texto dado en el nombre (búsqueda parcial)
     * @param nombre nombre o parte del nombre
     * @return List<Usuario>
     */
    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %?1%")
    List<Usuario> findByNombreContaining(String nombre);
    
    /**
     * Buscar usuarios que contengan el texto dado en el nombre (ignorando mayúsculas)
     * @param nombre nombre o parte del nombre
     * @return List<Usuario>
     */
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Obtener todos los usuarios ordenados por nombre
     * @return List<Usuario>
     */
    List<Usuario> findAllByOrderByNombreAsc();
    
    /**
     * Buscar usuarios por rol ordenados por nombre
     * @param rol rol del usuario
     * @return List<Usuario>
     */
    List<Usuario> findByRolOrderByNombreAsc(Rol rol);
    
    /**
     * Verificar si existe un usuario con el ID dado
     * @param idUsuario ID del usuario
     * @return true si existe, false si no
     */
    boolean existsByIdUsuario(String idUsuario);
    
    /**
     * Buscar meseros activos
     * @return List<Usuario>
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'Mesero' ORDER BY u.nombre")
    List<Usuario> findMeseros();
    
    /**
     * Buscar cocineros activos
     * @return List<Usuario>
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'Cocinero' ORDER BY u.nombre")
    List<Usuario> findCocineros();
}

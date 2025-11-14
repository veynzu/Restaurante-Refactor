package com.example.demo.repository;

import com.example.demo.entity.Telefono;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.UsuarioTelefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad UsuarioTelefono
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface UsuarioTelefonoRepository extends JpaRepository<UsuarioTelefono, Integer> {
    
    /**
     * Buscar relaciones por usuario
     * @param usuario usuario de la relación
     * @return List<UsuarioTelefono>
     */
    List<UsuarioTelefono> findByUsuario(Usuario usuario);
    
    /**
     * Buscar relaciones por teléfono
     * @param telefono teléfono de la relación
     * @return List<UsuarioTelefono>
     */
    List<UsuarioTelefono> findByTelefono(Telefono telefono);
    
    /**
     * Buscar relación específica por usuario y teléfono
     * @param usuario usuario de la relación
     * @param telefono teléfono de la relación
     * @return Optional<UsuarioTelefono>
     */
    Optional<UsuarioTelefono> findByUsuarioAndTelefono(Usuario usuario, Telefono telefono);
    
    /**
     * Verificar si existe una relación entre usuario y teléfono
     * @param usuario usuario de la relación
     * @param telefono teléfono de la relación
     * @return true si existe, false si no
     */
    boolean existsByUsuarioAndTelefono(Usuario usuario, Telefono telefono);
}

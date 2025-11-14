package com.example.demo.repository;

import com.example.demo.entity.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Telefono
 * Proporciona operaciones CRUD y consultas personalizadas
 */
@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Integer> {
    
    /**
     * Buscar teléfono por número
     * @param numero número de teléfono
     * @return Optional<Telefono>
     */
    Optional<Telefono> findByNumero(String numero);
    
    /**
     * Verificar si existe un teléfono con el número dado
     * @param numero número de teléfono
     * @return true si existe, false si no
     */
    boolean existsByNumero(String numero);
    
    /**
     * Buscar teléfonos que contengan el número dado
     * @param numero número o parte del número
     * @return List<Telefono>
     */
    List<Telefono> findByNumeroContaining(String numero);
}

package com.example.demo.service;

import com.example.demo.entity.Telefono;
import com.example.demo.repository.TelefonoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servicio para la gestión de teléfonos
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class TelefonoService {
    
    @Autowired
    private TelefonoRepository telefonoRepository;
    
    // Patrón para validar números de teléfono peruanos
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^[+]?[0-9]{7,15}$");
    
    /**
     * Obtener todos los teléfonos
     * @return Lista de todos los teléfonos
     */
    @Transactional(readOnly = true)
    public List<Telefono> obtenerTodosLosTelefonos() {
        return telefonoRepository.findAll();
    }
    
    /**
     * Obtener teléfono por ID
     * @param id ID del teléfono
     * @return Optional<Telefono>
     */
    @Transactional(readOnly = true)
    public Optional<Telefono> obtenerTelefonoPorId(Integer id) {
        return telefonoRepository.findById(id);
    }
    
    /**
     * Obtener teléfono por número
     * @param numero número del teléfono
     * @return Optional<Telefono>
     */
    @Transactional(readOnly = true)
    public Optional<Telefono> obtenerTelefonoPorNumero(String numero) {
        return telefonoRepository.findByNumero(numero);
    }
    
    /**
     * Crear un nuevo teléfono
     * @param telefono teléfono a crear
     * @return Telefono creado
     * @throws IllegalArgumentException si el teléfono ya existe o es inválido
     */
    public Telefono crearTelefono(Telefono telefono) {
        // Validar que el número no esté vacío
        if (telefono.getNumero() == null || telefono.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
        }
        
        String numeroNormalizado = normalizarNumero(telefono.getNumero().trim());
        
        // Validar formato del número
        if (!esNumeroValido(numeroNormalizado)) {
            throw new IllegalArgumentException("El número de teléfono no tiene un formato válido");
        }
        
        // Validar que no exista un teléfono con el mismo número
        if (telefonoRepository.existsByNumero(numeroNormalizado)) {
            throw new IllegalArgumentException("Ya existe un teléfono con el número: " + numeroNormalizado);
        }
        
        telefono.setNumero(numeroNormalizado);
        
        return telefonoRepository.save(telefono);
    }
    
    /**
     * Actualizar un teléfono existente
     * @param id ID del teléfono a actualizar
     * @param telefono teléfono con los nuevos datos
     * @return Telefono actualizado
     * @throws IllegalArgumentException si el teléfono no existe o hay conflictos
     */
    public Telefono actualizarTelefono(Integer id, Telefono telefono) {
        Telefono telefonoExistente = telefonoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Teléfono no encontrado con ID: " + id));
        
        // Validar que el número no esté vacío
        if (telefono.getNumero() == null || telefono.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
        }
        
        String numeroNormalizado = normalizarNumero(telefono.getNumero().trim());
        
        // Validar formato del número
        if (!esNumeroValido(numeroNormalizado)) {
            throw new IllegalArgumentException("El número de teléfono no tiene un formato válido");
        }
        
        // Validar que no exista otro teléfono con el mismo número
        if (!telefonoExistente.getNumero().equals(numeroNormalizado) && 
            telefonoRepository.existsByNumero(numeroNormalizado)) {
            throw new IllegalArgumentException("Ya existe un teléfono con el número: " + numeroNormalizado);
        }
        
        telefonoExistente.setNumero(numeroNormalizado);
        
        return telefonoRepository.save(telefonoExistente);
    }
    
    /**
     * Eliminar un teléfono
     * @param id ID del teléfono a eliminar
     * @throws IllegalArgumentException si el teléfono no existe
     * @throws IllegalStateException si el teléfono está siendo usado por usuarios
     */
    public void eliminarTelefono(Integer id) {
        Telefono telefono = telefonoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Teléfono no encontrado con ID: " + id));
        
        // TODO: Validar que el teléfono no esté siendo usado por usuarios
        // Por ahora solo eliminamos
        telefonoRepository.delete(telefono);
    }
    
    /**
     * Verificar si existe un teléfono con el número dado
     * @param numero número del teléfono
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existeTelefonoPorNumero(String numero) {
        return telefonoRepository.existsByNumero(numero);
    }
    
    /**
     * Buscar teléfonos que contengan el número dado
     * @param numero número a buscar
     * @return Lista de teléfonos que coinciden
     */
    @Transactional(readOnly = true)
    public List<Telefono> buscarTelefonosPorNumero(String numero) {
        return telefonoRepository.findByNumeroContaining(numero);
    }
    
    /**
     * Contar el total de teléfonos
     * @return número total de teléfonos
     */
    @Transactional(readOnly = true)
    public long contarTelefonos() {
        return telefonoRepository.count();
    }
    
    /**
     * Normalizar el número de teléfono
     * @param numero número a normalizar
     * @return número normalizado
     */
    private String normalizarNumero(String numero) {
        // Remover espacios, guiones y paréntesis
        String normalizado = numero.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Si no empieza con +, agregar código de país para Perú si es necesario
        if (!normalizado.startsWith("+")) {
            if (normalizado.length() == 9 && normalizado.startsWith("9")) {
                // Número celular peruano sin código de país
                normalizado = "+51" + normalizado;
            } else if (normalizado.length() == 7 && normalizado.startsWith("2")) {
                // Número fijo peruano sin código de país
                normalizado = "+51" + normalizado;
            }
        }
        
        return normalizado;
    }
    
    /**
     * Validar si el número de teléfono tiene un formato válido
     * @param numero número a validar
     * @return true si es válido, false si no
     */
    private boolean esNumeroValido(String numero) {
        if (numero == null || numero.length() < 7 || numero.length() > 15) {
            return false;
        }
        
        return PATRON_TELEFONO.matcher(numero).matches();
    }
    
    /**
     * Crear teléfono con validación automática
     * @param numero número del teléfono
     * @return Telefono creado
     */
    public Telefono crearTelefonoConNumero(String numero) {
        Telefono telefono = new Telefono();
        telefono.setNumero(numero);
        return crearTelefono(telefono);
    }
}

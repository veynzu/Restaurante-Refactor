package com.example.demo.service;

import com.example.demo.entity.Categoria;
import com.example.demo.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de categorías de productos
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 */
@Service
@Transactional
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    /**
     * Obtener todas las categorías
     * @return Lista de todas las categorías
     */
    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }
    
    /**
     * Obtener categoría por ID
     * @param id ID de la categoría
     * @return Optional<Categoria>
     */
    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id);
    }
    
    /**
     * Obtener categoría por nombre
     * @param nombre nombre de la categoría
     * @return Optional<Categoria>
     */
    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }
    
    /**
     * Crear una nueva categoría
     * @param categoria categoría a crear
     * @return Categoria creada
     * @throws IllegalArgumentException si la categoría ya existe
     */
    public Categoria crearCategoria(Categoria categoria) {
        // Validar que el nombre no esté vacío
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        // Validar longitud del nombre
        String nombreNormalizado = categoria.getNombre().trim();
        if (nombreNormalizado.length() > 20) {
            throw new IllegalArgumentException("El nombre de la categoría no puede exceder 20 caracteres");
        }
        
        // Validar que no exista una categoría con el mismo nombre
        if (categoriaRepository.existsByNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + nombreNormalizado);
        }
        
        // Capitalizar primera letra de cada palabra
        categoria.setNombre(capitalizarPalabras(nombreNormalizado));
        
        return categoriaRepository.save(categoria);
    }
    
    /**
     * Actualizar una categoría existente
     * @param id ID de la categoría a actualizar
     * @param categoria categoría con los nuevos datos
     * @return Categoria actualizada
     * @throws IllegalArgumentException si la categoría no existe o hay conflictos
     */
    public Categoria actualizarCategoria(Integer id, Categoria categoria) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        // Validar que el nombre no esté vacío
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        // Validar longitud del nombre
        String nombreNormalizado = categoria.getNombre().trim();
        if (nombreNormalizado.length() > 20) {
            throw new IllegalArgumentException("El nombre de la categoría no puede exceder 20 caracteres");
        }
        
        // Validar que no exista otra categoría con el mismo nombre
        String nombreCapitalizado = capitalizarPalabras(nombreNormalizado);
        if (!categoriaExistente.getNombre().equals(nombreCapitalizado) && 
            categoriaRepository.existsByNombre(nombreCapitalizado)) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + nombreCapitalizado);
        }
        
        // Actualizar el nombre
        categoriaExistente.setNombre(nombreCapitalizado);
        
        return categoriaRepository.save(categoriaExistente);
    }
    
    /**
     * Eliminar una categoría
     * @param id ID de la categoría a eliminar
     * @throws IllegalArgumentException si la categoría no existe
     * @throws IllegalStateException si la categoría está siendo usada por productos
     */
    public void eliminarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        // TODO: Validar que la categoría no esté siendo usada por productos
        // Por ahora solo eliminamos
        categoriaRepository.delete(categoria);
    }
    
    /**
     * Verificar si existe una categoría con el nombre dado
     * @param nombre nombre de la categoría
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existeCategoriaPorNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
    
    /**
     * Buscar categorías que contengan el texto dado
     * @param texto texto a buscar
     * @return Lista de categorías que coinciden
     */
    @Transactional(readOnly = true)
    public List<Categoria> buscarCategoriasPorTexto(String texto) {
        return categoriaRepository.findByNombreContaining(texto);
    }
    
    /**
     * Obtener todas las categorías ordenadas por nombre
     * @return Lista de categorías ordenada alfabéticamente
     */
    @Transactional(readOnly = true)
    public List<Categoria> obtenerCategoriasOrdenadas() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }
    
    /**
     * Contar el total de categorías
     * @return número total de categorías
     */
    @Transactional(readOnly = true)
    public long contarCategorias() {
        return categoriaRepository.count();
    }
    
    /**
     * Crear categorías básicas del sistema si no existen
     */
    public void crearCategoriasBasicas() {
        String[] categoriasBasicas = {"ENTRADAS", "PLATOS FUERTES", "BEBIDAS", "POSTRES", "ENSALADAS"};
        
        for (String nombreCategoria : categoriasBasicas) {
            if (!categoriaRepository.existsByNombre(nombreCategoria)) {
                Categoria categoria = new Categoria(nombreCategoria);
                categoriaRepository.save(categoria);
            }
        }
    }
    
    /**
     * Capitalizar la primera letra de cada palabra
     * @param texto texto a capitalizar
     * @return texto capitalizado
     */
    private String capitalizarPalabras(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        
        String[] palabras = texto.toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < palabras.length; i++) {
            if (!palabras[i].isEmpty()) {
                palabras[i] = palabras[i].substring(0, 1).toUpperCase() + palabras[i].substring(1);
                if (i > 0) {
                    resultado.append(" ");
                }
                resultado.append(palabras[i]);
            }
        }
        
        return resultado.toString();
    }
}

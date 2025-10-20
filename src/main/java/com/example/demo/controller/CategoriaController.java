package com.example.demo.controller;

import com.example.demo.entity.Categoria;
import com.example.demo.service.CategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestión de categorías
 * Expone endpoints para operaciones CRUD sobre categorías
 */
@Tag(name = "Categorías", description = "API para la gestión de categorías de productos (Entradas, Platos Fuertes, Bebidas, Postres)")
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    /**
     * Obtener todas las categorías
     * GET /api/categorias
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodasLasCategorias() {
        try {
            List<Categoria> categorias = categoriaService.obtenerCategoriasOrdenadas();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener categoría por ID
     * GET /api/categorias/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Integer id) {
        try {
            Optional<Categoria> categoria = categoriaService.obtenerCategoriaPorId(id);
            return categoria.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener categoría por nombre
     * GET /api/categorias/nombre/{nombre}
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Categoria> obtenerCategoriaPorNombre(@PathVariable String nombre) {
        try {
            Optional<Categoria> categoria = categoriaService.obtenerCategoriaPorNombre(nombre);
            return categoria.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear una nueva categoría
     * POST /api/categorias
     */
    @PostMapping
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody Categoria categoria) {
        try {
            Categoria categoriaCreada = categoriaService.crearCategoria(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCreada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar una categoría existente
     * PUT /api/categorias/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Integer id, @Valid @RequestBody Categoria categoria) {
        try {
            Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar una categoría
     * DELETE /api/categorias/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.ok(Map.of("message", "Categoría eliminada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Buscar categorías por texto
     * GET /api/categorias/buscar?texto={texto}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Categoria>> buscarCategorias(@RequestParam String texto) {
        try {
            List<Categoria> categorias = categoriaService.buscarCategoriasPorTexto(texto);
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verificar si existe una categoría por nombre
     * GET /api/categorias/existe/{nombre}
     */
    @GetMapping("/existe/{nombre}")
    public ResponseEntity<Map<String, Boolean>> existeCategoriaPorNombre(@PathVariable String nombre) {
        try {
            boolean existe = categoriaService.existeCategoriaPorNombre(nombre);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de categorías
     * GET /api/categorias/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarCategorias() {
        try {
            long count = categoriaService.contarCategorias();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear categorías básicas del sistema
     * POST /api/categorias/basicas
     */
    @PostMapping("/basicas")
    public ResponseEntity<?> crearCategoriasBasicas() {
        try {
            categoriaService.crearCategoriasBasicas();
            return ResponseEntity.ok(Map.of("message", "Categorías básicas creadas exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}

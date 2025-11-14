package com.example.demo.controller;

import com.example.demo.dto.request.ProductoCreateRequestDTO;
import com.example.demo.dto.response.ProductoResponseDTO;
import com.example.demo.entity.Producto;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de productos
 * Expone endpoints para operaciones CRUD sobre productos
 */
@Tag(name = "Productos", description = "API para la gestión de productos del menú (CRUD, stock, categorías, precios)")
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ProductoMapper productoMapper;
    
    /**
     * Obtener todos los productos
     * GET /api/productos
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodosLosProductos() {
        try {
            List<Producto> productos = productoService.obtenerTodosLosProductos();
            List<ProductoResponseDTO> productosDTO = productos.stream()
                    .map(productoMapper::toResponseDTO)
                    .collect(Collectors.toList());
            
            // Log para verificar qué se está enviando
            System.out.println("📤 Controller - Enviando " + productosDTO.size() + " productos");
            for (ProductoResponseDTO dto : productosDTO) {
                System.out.println("📤 Controller - Producto ID: " + dto.getIdProducto() + 
                    ", Categoría: " + dto.getCategoria() + 
                    ", idCategoria: " + dto.getIdCategoria());
            }
            
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener producto por ID
     * GET /api/productos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Integer id) {
        try {
            Optional<Producto> producto = productoService.obtenerProductoPorId(id);
            return producto.map(p -> ResponseEntity.ok(productoMapper.toResponseDTO(p)))
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener productos por categoría
     * GET /api/productos/categoria/{idCategoria}
     */
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Integer idCategoria) {
        try {
            List<Producto> productos = productoService.obtenerProductosPorCategoria(idCategoria);
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener productos activos
     * GET /api/productos/activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> obtenerProductosActivos() {
        try {
            List<Producto> productos = productoService.obtenerProductosActivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener productos inactivos
     * GET /api/productos/inactivos
     */
    @GetMapping("/inactivos")
    public ResponseEntity<List<Producto>> obtenerProductosInactivos() {
        try {
            List<Producto> productos = productoService.obtenerProductosInactivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear un nuevo producto
     * POST /api/productos
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto productoCreado = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Crear producto con datos básicos
     * POST /api/productos/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearProductoConDatos(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("📥 Recibida petición para crear producto");
            System.out.println("📋 Request completo: " + request.toString());
            
            String nombre = (String) request.get("nombre");
            System.out.println("📋 Nombre: " + nombre);
            
            // Manejar precio - puede venir como Number, String, etc.
            BigDecimal precio;
            Object precioObj = request.get("precio");
            if (precioObj == null) {
                precio = null;
            } else if (precioObj instanceof Number) {
                precio = BigDecimal.valueOf(((Number) precioObj).doubleValue());
            } else {
                precio = new BigDecimal(precioObj.toString());
            }
            System.out.println("📋 Precio: " + precio);
            
            // Manejar stock - puede venir como Integer, Long, Double, etc.
            Integer stock = null;
            Object stockObj = request.get("stock");
            if (stockObj != null) {
                if (stockObj instanceof Number) {
                    stock = ((Number) stockObj).intValue();
                } else {
                    stock = Integer.parseInt(stockObj.toString());
                }
            }
            System.out.println("📋 Stock: " + stock);
            
            // Manejar idCategoria - puede venir como Integer, Long, Double, etc.
            Integer idCategoria = null;
            Object idCategoriaObj = request.get("idCategoria");
            System.out.println("📋 idCategoria recibido (raw): " + idCategoriaObj);
            System.out.println("📋 Tipo de idCategoria: " + (idCategoriaObj != null ? idCategoriaObj.getClass().getName() : "null"));
            
            if (idCategoriaObj != null) {
                if (idCategoriaObj instanceof Number) {
                    idCategoria = ((Number) idCategoriaObj).intValue();
                } else {
                    idCategoria = Integer.parseInt(idCategoriaObj.toString());
                }
            }
            System.out.println("📋 idCategoria procesado: " + idCategoria);
            
            if (nombre == null || nombre.trim().isEmpty() || precio == null || stock == null || idCategoria == null) {
                System.out.println("❌ Validación fallida - campos faltantes");
                return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son obligatorios"));
            }
            
            System.out.println("✅ Todos los campos válidos, creando producto...");
            Producto productoCreado = productoService.crearProductoConDatos(nombre.trim(), precio, stock, idCategoria);
            System.out.println("✅ Producto creado con ID: " + productoCreado.getIdProducto());
            System.out.println("✅ Categoría asignada: " + (productoCreado.getCategoria() != null ? productoCreado.getCategoria().getNombre() : "null"));
            
            ProductoResponseDTO productoDTO = productoMapper.toResponseDTO(productoCreado);
            System.out.println("✅ DTO creado con categoría: " + productoDTO.getCategoria());
            return ResponseEntity.status(HttpStatus.CREATED).body(productoDTO);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error en el formato de los números: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Log para debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar un producto existente
     * PUT /api/productos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer id, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar un producto
     * DELETE /api/productos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok(Map.of("message", "Producto eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Activar un producto
     * PUT /api/productos/{id}/activar
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activarProducto(@PathVariable Integer id) {
        try {
            Producto productoActualizado = productoService.activarProducto(id);
            ProductoResponseDTO productoDTO = productoMapper.toResponseDTO(productoActualizado);
            return ResponseEntity.ok(productoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Desactivar un producto
     * PUT /api/productos/{id}/desactivar
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarProducto(@PathVariable Integer id) {
        try {
            Producto productoActualizado = productoService.desactivarProducto(id);
            ProductoResponseDTO productoDTO = productoMapper.toResponseDTO(productoActualizado);
            return ResponseEntity.ok(productoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar stock de un producto
     * PUT /api/productos/{id}/stock
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer nuevoStock = request.get("stock");
            if (nuevoStock == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El stock es obligatorio"));
            }
            
            Producto productoActualizado = productoService.actualizarStock(id, nuevoStock);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Reducir stock de un producto
     * PUT /api/productos/{id}/reducir-stock
     */
    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<?> reducirStock(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer cantidad = request.get("cantidad");
            if (cantidad == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cantidad es obligatoria"));
            }
            
            Producto productoActualizado = productoService.reducirStock(id, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Aumentar stock de un producto
     * PUT /api/productos/{id}/aumentar-stock
     */
    @PutMapping("/{id}/aumentar-stock")
    public ResponseEntity<?> aumentarStock(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer cantidad = request.get("cantidad");
            if (cantidad == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cantidad es obligatoria"));
            }
            
            Producto productoActualizado = productoService.aumentarStock(id, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Buscar productos por nombre
     * GET /api/productos/buscar?nombre={nombre}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoService.buscarProductosPorNombre(nombre);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Buscar productos por rango de precio
     * GET /api/productos/precio?minimo={minimo}&maximo={maximo}
     */
    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> buscarProductosPorPrecio(
            @RequestParam BigDecimal minimo, 
            @RequestParam BigDecimal maximo) {
        try {
            List<Producto> productos = productoService.buscarProductosPorRangoPrecio(minimo, maximo);
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener productos con stock bajo
     * GET /api/productos/stock-bajo/{stockMinimo}
     */
    @GetMapping("/stock-bajo/{stockMinimo}")
    public ResponseEntity<List<Producto>> obtenerProductosConStockBajo(@PathVariable Integer stockMinimo) {
        try {
            List<Producto> productos = productoService.obtenerProductosConStockBajo(stockMinimo);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verificar si existe un producto por nombre
     * GET /api/productos/existe/{nombre}
     */
    @GetMapping("/existe/{nombre}")
    public ResponseEntity<Map<String, Boolean>> existeProductoPorNombre(@PathVariable String nombre) {
        try {
            boolean existe = productoService.existeProductoPorNombre(nombre);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de productos
     * GET /api/productos/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarProductos() {
        try {
            long count = productoService.contarProductos();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar productos por categoría
     * GET /api/productos/count/categoria/{idCategoria}
     */
    @GetMapping("/count/categoria/{idCategoria}")
    public ResponseEntity<Map<String, Long>> contarProductosPorCategoria(@PathVariable Integer idCategoria) {
        try {
            long count = productoService.contarProductosPorCategoria(idCategoria);
            return ResponseEntity.ok(Map.of("total", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

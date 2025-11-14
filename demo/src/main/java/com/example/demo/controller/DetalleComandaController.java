package com.example.demo.controller;

import com.example.demo.entity.DetalleComanda;
import com.example.demo.service.DetalleComandaService;
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

/**
 * Controlador REST para la gestión de detalles de comandas
 * Expone endpoints para operaciones CRUD sobre detalles de comandas
 */
@Tag(name = "Detalles de Comanda", description = "API para gestionar productos en comandas (agregar, modificar, calcular totales)")
@RestController
@RequestMapping("/api/detalle-comandas")
@CrossOrigin(origins = "*")
public class DetalleComandaController {
    
    @Autowired
    private DetalleComandaService detalleComandaService;
    
    /**
     * Obtener todos los detalles de comandas
     * GET /api/detalle-comandas
     */
    @GetMapping
    public ResponseEntity<List<DetalleComanda>> obtenerTodosLosDetalles() {
        try {
            List<DetalleComanda> detalles = detalleComandaService.obtenerTodosLosDetalles();
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener detalle por ID
     * GET /api/detalle-comandas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetalleComanda> obtenerDetallePorId(@PathVariable Integer id) {
        try {
            Optional<DetalleComanda> detalle = detalleComandaService.obtenerDetallePorId(id);
            return detalle.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener detalles por comanda
     * GET /api/detalle-comandas/comanda/{idComanda}
     */
    @GetMapping("/comanda/{idComanda}")
    public ResponseEntity<List<DetalleComanda>> obtenerDetallesPorComanda(@PathVariable Integer idComanda) {
        try {
            List<DetalleComanda> detalles = detalleComandaService.obtenerDetallesPorComanda(idComanda);
            return ResponseEntity.ok(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener detalles por producto
     * GET /api/detalle-comandas/producto/{idProducto}
     */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<DetalleComanda>> obtenerDetallesPorProducto(@PathVariable Integer idProducto) {
        try {
            List<DetalleComanda> detalles = detalleComandaService.obtenerDetallesPorProducto(idProducto);
            return ResponseEntity.ok(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener detalles por estado
     * GET /api/detalle-comandas/estado/{idEstado}
     */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<DetalleComanda>> obtenerDetallesPorEstado(@PathVariable Integer idEstado) {
        try {
            List<DetalleComanda> detalles = detalleComandaService.obtenerDetallesPorEstado(idEstado);
            return ResponseEntity.ok(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear un nuevo detalle de comanda
     * POST /api/detalle-comandas
     */
    @PostMapping
    public ResponseEntity<?> crearDetalle(@Valid @RequestBody DetalleComanda detalle) {
        try {
            DetalleComanda detalleCreado = detalleComandaService.crearDetalle(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(detalleCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Crear detalle con datos básicos
     * POST /api/detalle-comandas/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearDetalleConDatos(@RequestBody Map<String, Object> request) {
        try {
            Integer idComanda = (Integer) request.get("idComanda");
            Integer idProducto = (Integer) request.get("idProducto");
            Integer cantidad = (Integer) request.get("cantidad");
            BigDecimal precioUnitario = new BigDecimal(request.get("precioUnitario").toString());
            
            if (idComanda == null || idProducto == null || cantidad == null || precioUnitario == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son obligatorios"));
            }
            
            DetalleComanda detalleCreado = detalleComandaService.crearDetalleConDatos(idComanda, idProducto, cantidad, precioUnitario);
            return ResponseEntity.status(HttpStatus.CREATED).body(detalleCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar un detalle existente
     * PUT /api/detalle-comandas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDetalle(@PathVariable Integer id, @Valid @RequestBody DetalleComanda detalle) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.actualizarDetalle(id, detalle);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar un detalle
     * DELETE /api/detalle-comandas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Integer id) {
        try {
            detalleComandaService.eliminarDetalle(id);
            return ResponseEntity.ok(Map.of("message", "Detalle eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Cambiar estado de un detalle
     * PUT /api/detalle-comandas/{id}/estado/{idEstado}
     */
    @PutMapping("/{id}/estado/{idEstado}")
    public ResponseEntity<?> cambiarEstadoDetalle(@PathVariable Integer id, @PathVariable Integer idEstado) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.cambiarEstadoDetalle(id, idEstado);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar detalle como pendiente
     * PUT /api/detalle-comandas/{id}/pendiente
     */
    @PutMapping("/{id}/pendiente")
    public ResponseEntity<?> marcarDetalleComoPendiente(@PathVariable Integer id) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.marcarDetalleComoPendiente(id);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar detalle como en preparación
     * PUT /api/detalle-comandas/{id}/preparacion
     */
    @PutMapping("/{id}/preparacion")
    public ResponseEntity<?> marcarDetalleComoEnPreparacion(@PathVariable Integer id) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.marcarDetalleComoEnPreparacion(id);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar detalle como completado
     * PUT /api/detalle-comandas/{id}/completado
     */
    @PutMapping("/{id}/completado")
    public ResponseEntity<?> marcarDetalleComoCompletado(@PathVariable Integer id) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.marcarDetalleComoCompletado(id);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar detalle como cancelado
     * PUT /api/detalle-comandas/{id}/cancelado
     */
    @PutMapping("/{id}/cancelado")
    public ResponseEntity<?> marcarDetalleComoCancelado(@PathVariable Integer id) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.marcarDetalleComoCancelado(id);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar cantidad de un detalle
     * PUT /api/detalle-comandas/{id}/cantidad
     */
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer nuevaCantidad = request.get("cantidad");
            if (nuevaCantidad == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La cantidad es obligatoria"));
            }
            
            DetalleComanda detalleActualizado = detalleComandaService.actualizarCantidad(id, nuevaCantidad);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar precio unitario de un detalle
     * PUT /api/detalle-comandas/{id}/precio
     */
    @PutMapping("/{id}/precio")
    public ResponseEntity<?> actualizarPrecioUnitario(@PathVariable Integer id, @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal nuevoPrecio = request.get("precio");
            if (nuevoPrecio == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El precio es obligatorio"));
            }
            
            DetalleComanda detalleActualizado = detalleComandaService.actualizarPrecioUnitario(id, nuevoPrecio);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Recalcular subtotal de un detalle
     * PUT /api/detalle-comandas/{id}/recalcular
     */
    @PutMapping("/{id}/recalcular")
    public ResponseEntity<?> recalcularSubtotal(@PathVariable Integer id) {
        try {
            DetalleComanda detalleActualizado = detalleComandaService.recalcularSubtotal(id);
            return ResponseEntity.ok(detalleActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Obtener subtotal de una comanda
     * GET /api/detalle-comandas/subtotal/{idComanda}
     */
    @GetMapping("/subtotal/{idComanda}")
    public ResponseEntity<Map<String, BigDecimal>> obtenerSubtotalComanda(@PathVariable Integer idComanda) {
        try {
            BigDecimal subtotal = detalleComandaService.obtenerSubtotalComanda(idComanda);
            return ResponseEntity.ok(Map.of("subtotal", subtotal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de detalles
     * GET /api/detalle-comandas/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarDetalles() {
        try {
            long count = detalleComandaService.contarDetalles();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar detalles por estado
     * GET /api/detalle-comandas/count/estado/{idEstado}
     */
    @GetMapping("/count/estado/{idEstado}")
    public ResponseEntity<Map<String, Long>> contarDetallesPorEstado(@PathVariable Integer idEstado) {
        try {
            long count = detalleComandaService.contarDetallesPorEstado(idEstado);
            return ResponseEntity.ok(Map.of("total", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar detalles por comanda
     * GET /api/detalle-comandas/count/comanda/{idComanda}
     */
    @GetMapping("/count/comanda/{idComanda}")
    public ResponseEntity<Map<String, Long>> contarDetallesPorComanda(@PathVariable Integer idComanda) {
        try {
            long count = detalleComandaService.contarDetallesPorComanda(idComanda);
            return ResponseEntity.ok(Map.of("total", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

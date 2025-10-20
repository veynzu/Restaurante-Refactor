package com.example.demo.controller;

import com.example.demo.dto.request.ComandaCreateRequestDTO;
import com.example.demo.dto.response.ComandaResponseDTO;
import com.example.demo.entity.Comanda;
import com.example.demo.mapper.ComandaMapper;
import com.example.demo.service.ComandaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de comandas
 * Expone endpoints para operaciones CRUD sobre comandas
 */
@Tag(name = "Comandas", description = "API para la gestión de pedidos/comandas (crear, asignar, cambiar estados, reportes)")
@RestController
@RequestMapping("/api/comandas")
@CrossOrigin(origins = "*")
public class ComandaController {
    
    @Autowired
    private ComandaService comandaService;
    
    @Autowired
    private ComandaMapper comandaMapper;
    
    /**
     * Obtener todas las comandas
     * GET /api/comandas
     */
    @GetMapping
    public ResponseEntity<List<ComandaResponseDTO>> obtenerTodasLasComandas() {
        try {
            List<Comanda> comandas = comandaService.obtenerTodasLasComandas();
            List<ComandaResponseDTO> comandasDTO = comandas.stream()
                    .map(comandaMapper::toResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(comandasDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comanda por ID
     * GET /api/comandas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comanda> obtenerComandaPorId(@PathVariable Integer id) {
        try {
            Optional<Comanda> comanda = comandaService.obtenerComandaPorId(id);
            return comanda.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comandas por mesa
     * GET /api/comandas/mesa/{idMesa}
     */
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<Comanda>> obtenerComandasPorMesa(@PathVariable Integer idMesa) {
        try {
            List<Comanda> comandas = comandaService.obtenerComandasPorMesa(idMesa);
            return ResponseEntity.ok(comandas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comandas por mesero
     * GET /api/comandas/mesero/{idMesero}
     */
    @GetMapping("/mesero/{idMesero}")
    public ResponseEntity<List<Comanda>> obtenerComandasPorMesero(@PathVariable String idMesero) {
        try {
            List<Comanda> comandas = comandaService.obtenerComandasPorMesero(idMesero);
            return ResponseEntity.ok(comandas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comandas por cocinero
     * GET /api/comandas/cocinero/{idCocinero}
     */
    @GetMapping("/cocinero/{idCocinero}")
    public ResponseEntity<List<Comanda>> obtenerComandasPorCocinero(@PathVariable String idCocinero) {
        try {
            List<Comanda> comandas = comandaService.obtenerComandasPorCocinero(idCocinero);
            return ResponseEntity.ok(comandas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comandas por estado
     * GET /api/comandas/estado/{idEstado}
     */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<Comanda>> obtenerComandasPorEstado(@PathVariable Integer idEstado) {
        try {
            List<Comanda> comandas = comandaService.obtenerComandasPorEstado(idEstado);
            return ResponseEntity.ok(comandas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comandas pendientes
     * GET /api/comandas/pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<Comanda>> obtenerComandasPendientes() {
        try {
            List<Comanda> comandas = comandaService.obtenerComandasPendientes();
            return ResponseEntity.ok(comandas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comandas en preparación
     * GET /api/comandas/preparacion
     */
    @GetMapping("/preparacion")
    public ResponseEntity<List<Comanda>> obtenerComandasEnPreparacion() {
        try {
            List<Comanda> comandas = comandaService.obtenerComandasEnPreparacion();
            return ResponseEntity.ok(comandas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear una nueva comanda
     * POST /api/comandas
     */
    @PostMapping
    public ResponseEntity<?> crearComanda(@Valid @RequestBody Comanda comanda) {
        try {
            Comanda comandaCreada = comandaService.crearComanda(comanda);
            return ResponseEntity.status(HttpStatus.CREATED).body(comandaCreada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Crear comanda con datos básicos
     * POST /api/comandas/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearComandaConDatos(@RequestBody Map<String, Object> request) {
        try {
            Integer idMesa = (Integer) request.get("idMesa");
            String idMesero = (String) request.get("idMesero");
            
            if (idMesa == null || idMesero == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mesa y mesero son obligatorios"));
            }
            
            Comanda comandaCreada = comandaService.crearComandaConDatos(idMesa, idMesero);
            return ResponseEntity.status(HttpStatus.CREATED).body(comandaCreada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar una comanda existente
     * PUT /api/comandas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarComanda(@PathVariable Integer id, @Valid @RequestBody Comanda comanda) {
        try {
            Comanda comandaActualizada = comandaService.actualizarComanda(id, comanda);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar una comanda
     * DELETE /api/comandas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarComanda(@PathVariable Integer id) {
        try {
            comandaService.eliminarComanda(id);
            return ResponseEntity.ok(Map.of("message", "Comanda eliminada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Cambiar estado de una comanda
     * PUT /api/comandas/{id}/estado/{idEstado}
     */
    @PutMapping("/{id}/estado/{idEstado}")
    public ResponseEntity<?> cambiarEstadoComanda(@PathVariable Integer id, @PathVariable Integer idEstado) {
        try {
            Comanda comandaActualizada = comandaService.cambiarEstadoComanda(id, idEstado);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Asignar cocinero a una comanda
     * PUT /api/comandas/{id}/asignar-cocinero/{idCocinero}
     */
    @PutMapping("/{id}/asignar-cocinero/{idCocinero}")
    public ResponseEntity<?> asignarCocinero(@PathVariable Integer id, @PathVariable String idCocinero) {
        try {
            Comanda comandaActualizada = comandaService.asignarCocinero(id, idCocinero);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar comanda como pendiente
     * PUT /api/comandas/{id}/pendiente
     */
    @PutMapping("/{id}/pendiente")
    public ResponseEntity<?> marcarComandaComoPendiente(@PathVariable Integer id) {
        try {
            Comanda comandaActualizada = comandaService.marcarComandaComoPendiente(id);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar comanda como en preparación
     * PUT /api/comandas/{id}/preparacion/{idCocinero}
     */
    @PutMapping("/{id}/preparacion/{idCocinero}")
    public ResponseEntity<?> marcarComandaComoEnPreparacion(@PathVariable Integer id, @PathVariable String idCocinero) {
        try {
            Comanda comandaActualizada = comandaService.marcarComandaComoEnPreparacion(id, idCocinero);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar comanda como completada
     * PUT /api/comandas/{id}/completada
     */
    @PutMapping("/{id}/completada")
    public ResponseEntity<?> marcarComandaComoCompletada(@PathVariable Integer id) {
        try {
            Comanda comandaActualizada = comandaService.marcarComandaComoCompletada(id);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Marcar comanda como cancelada
     * PUT /api/comandas/{id}/cancelada
     */
    @PutMapping("/{id}/cancelada")
    public ResponseEntity<?> marcarComandaComoCancelada(@PathVariable Integer id) {
        try {
            Comanda comandaActualizada = comandaService.marcarComandaComoCancelada(id);
            return ResponseEntity.ok(comandaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Obtener comandas por rango de fechas
     * GET /api/comandas/fechas?inicio={inicio}&fin={fin}
     */
    @GetMapping("/fechas")
    public ResponseEntity<List<Comanda>> obtenerComandasPorRangoFechas(
            @RequestParam String inicio, 
            @RequestParam String fin) {
        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(inicio);
            LocalDateTime fechaFin = LocalDateTime.parse(fin);
            
            List<Comanda> comandas = comandaService.obtenerComandasPorRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(comandas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener total de ventas por rango de fechas
     * GET /api/comandas/ventas?inicio={inicio}&fin={fin}
     */
    @GetMapping("/ventas")
    public ResponseEntity<Map<String, Double>> obtenerTotalVentasPorRangoFechas(
            @RequestParam String inicio, 
            @RequestParam String fin) {
        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(inicio);
            LocalDateTime fechaFin = LocalDateTime.parse(fin);
            
            Double totalVentas = comandaService.obtenerTotalVentasPorRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(Map.of("totalVentas", totalVentas));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de comandas
     * GET /api/comandas/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarComandas() {
        try {
            long count = comandaService.contarComandas();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar comandas por estado
     * GET /api/comandas/count/estado/{idEstado}
     */
    @GetMapping("/count/estado/{idEstado}")
    public ResponseEntity<Map<String, Long>> contarComandasPorEstado(@PathVariable Integer idEstado) {
        try {
            long count = comandaService.contarComandasPorEstado(idEstado);
            return ResponseEntity.ok(Map.of("total", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

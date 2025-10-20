package com.example.demo.controller;

import com.example.demo.dto.request.MesaCreateRequestDTO;
import com.example.demo.dto.response.MesaResponseDTO;
import com.example.demo.entity.Mesa;
import com.example.demo.mapper.MesaMapper;
import com.example.demo.service.MesaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de mesas
 * Expone endpoints para operaciones CRUD sobre mesas
 */
@Tag(name = "Mesas", description = "API para la gestión de mesas del restaurante (disponibilidad, capacidad, estados)")
@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaController {
    
    @Autowired
    private MesaService mesaService;
    
    @Autowired
    private MesaMapper mesaMapper;
    
    /**
     * Obtener todas las mesas
     * GET /api/mesas
     */
    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> obtenerTodasLasMesas() {
        try {
            List<Mesa> mesas = mesaService.obtenerTodasLasMesas();
            List<MesaResponseDTO> mesasDTO = mesas.stream()
                    .map(mesaMapper::toResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(mesasDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener mesa por ID
     * GET /api/mesas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerMesaPorId(@PathVariable Integer id) {
        try {
            Optional<Mesa> mesa = mesaService.obtenerMesaPorId(id);
            return mesa.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener mesas por estado
     * GET /api/mesas/estado/{idEstado}
     */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<Mesa>> obtenerMesasPorEstado(@PathVariable Integer idEstado) {
        try {
            List<Mesa> mesas = mesaService.obtenerMesasPorEstado(idEstado);
            return ResponseEntity.ok(mesas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener mesas disponibles
     * GET /api/mesas/disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Mesa>> obtenerMesasDisponibles() {
        try {
            List<Mesa> mesas = mesaService.obtenerMesasDisponibles();
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear una nueva mesa
     * POST /api/mesas
     */
    @PostMapping
    public ResponseEntity<?> crearMesa(@Valid @RequestBody Mesa mesa) {
        try {
            Mesa mesaCreada = mesaService.crearMesa(mesa);
            return ResponseEntity.status(HttpStatus.CREATED).body(mesaCreada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Crear mesa con datos básicos
     * POST /api/mesas/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearMesaConDatos(@RequestBody Map<String, Object> request) {
        try {
            Integer capacidad = (Integer) request.get("capacidad");
            String ubicacion = (String) request.get("ubicacion");
            String nombreEstado = (String) request.get("estado");
            
            if (capacidad == null || ubicacion == null || nombreEstado == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Capacidad, ubicación y estado son obligatorios"));
            }
            
            Mesa mesaCreada = mesaService.crearMesaConDatos(capacidad, ubicacion, nombreEstado);
            return ResponseEntity.status(HttpStatus.CREATED).body(mesaCreada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar una mesa existente
     * PUT /api/mesas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMesa(@PathVariable Integer id, @Valid @RequestBody Mesa mesa) {
        try {
            Mesa mesaActualizada = mesaService.actualizarMesa(id, mesa);
            return ResponseEntity.ok(mesaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar una mesa
     * DELETE /api/mesas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMesa(@PathVariable Integer id) {
        try {
            mesaService.eliminarMesa(id);
            return ResponseEntity.ok(Map.of("message", "Mesa eliminada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Cambiar estado de una mesa
     * PUT /api/mesas/{id}/estado/{idEstado}
     */
    @PutMapping("/{id}/estado/{idEstado}")
    public ResponseEntity<?> cambiarEstadoMesa(@PathVariable Integer id, @PathVariable Integer idEstado) {
        try {
            Mesa mesaActualizada = mesaService.cambiarEstadoMesa(id, idEstado);
            return ResponseEntity.ok(mesaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Ocupar una mesa
     * PUT /api/mesas/{id}/ocupar
     */
    @PutMapping("/{id}/ocupar")
    public ResponseEntity<?> ocuparMesa(@PathVariable Integer id) {
        try {
            Mesa mesaActualizada = mesaService.ocuparMesa(id);
            return ResponseEntity.ok(mesaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Liberar una mesa
     * PUT /api/mesas/{id}/liberar
     */
    @PutMapping("/{id}/liberar")
    public ResponseEntity<?> liberarMesa(@PathVariable Integer id) {
        try {
            Mesa mesaActualizada = mesaService.liberarMesa(id);
            return ResponseEntity.ok(mesaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Reservar una mesa
     * PUT /api/mesas/{id}/reservar
     */
    @PutMapping("/{id}/reservar")
    public ResponseEntity<?> reservarMesa(@PathVariable Integer id) {
        try {
            Mesa mesaActualizada = mesaService.reservarMesa(id);
            return ResponseEntity.ok(mesaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Buscar mesas por ubicación
     * GET /api/mesas/buscar?ubicacion={ubicacion}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Mesa>> buscarMesasPorUbicacion(@RequestParam String ubicacion) {
        try {
            List<Mesa> mesas = mesaService.buscarMesasPorUbicacion(ubicacion);
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Buscar mesas por capacidad mínima
     * GET /api/mesas/capacidad/{capacidadMinima}
     */
    @GetMapping("/capacidad/{capacidadMinima}")
    public ResponseEntity<List<Mesa>> buscarMesasPorCapacidadMinima(@PathVariable Integer capacidadMinima) {
        try {
            List<Mesa> mesas = mesaService.buscarMesasPorCapacidadMinima(capacidadMinima);
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener mesas disponibles por capacidad
     * GET /api/mesas/disponibles/capacidad/{capacidad}
     */
    @GetMapping("/disponibles/capacidad/{capacidad}")
    public ResponseEntity<List<Mesa>> obtenerMesasDisponiblesPorCapacidad(@PathVariable Integer capacidad) {
        try {
            List<Mesa> mesas = mesaService.obtenerMesasDisponiblesPorCapacidad(capacidad);
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar total de mesas
     * GET /api/mesas/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarMesas() {
        try {
            long count = mesaService.contarMesas();
            return ResponseEntity.ok(Map.of("total", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Contar mesas por estado
     * GET /api/mesas/count/estado/{idEstado}
     */
    @GetMapping("/count/estado/{idEstado}")
    public ResponseEntity<Map<String, Long>> contarMesasPorEstado(@PathVariable Integer idEstado) {
        try {
            long count = mesaService.contarMesasPorEstado(idEstado);
            return ResponseEntity.ok(Map.of("total", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

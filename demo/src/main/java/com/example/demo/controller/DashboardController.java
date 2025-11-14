package com.example.demo.controller;

import com.example.demo.entity.Comanda;
import com.example.demo.entity.Producto;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para el dashboard del panel de control
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ComandaRepository comandaRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private DetalleComandaRepository detalleComandaRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    /**
     * Endpoint de prueba para verificar autenticación
     * GET /api/dashboard/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint de prueba accesible");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener estadísticas del dashboard
     * GET /api/dashboard/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Total de mesas
            long totalMesas = mesaRepository.count();
            
            // Mesas ocupadas
            long mesasOcupadas = 0;
            var estadoOcupado = estadoRepository.findByNombre("Ocupado").orElse(null);
            var estadoOcupada = estadoRepository.findByNombre("Ocupada").orElse(null);
            if (estadoOcupado != null) {
                mesasOcupadas = mesaRepository.countByEstado(estadoOcupado);
            } else if (estadoOcupada != null) {
                mesasOcupadas = mesaRepository.countByEstado(estadoOcupada);
            }
            
            // Meseros activos (usuarios con rol Mesero)
            long meserosActivos = usuarioRepository.findByNombreRol("Mesero").size();
            
            // Órdenes en preparación (comandas con estado "En Preparacion")
            long ordenesEnPreparacion = comandaRepository.findByNombreEstado("En Preparacion").size();
            
            // Ventas del día (suma de totales de comandas completadas hoy)
            LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime finDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            double ventasHoyDouble = comandaRepository.findByFechaBetween(inicioDia, finDia).stream()
                .filter(c -> {
                    String nombreEstado = c.getEstado().getNombre();
                    return "Completado".equals(nombreEstado) || "Completada".equals(nombreEstado);
                })
                .mapToDouble(c -> {
                    Double total = c.calcularTotal();
                    return total != null ? total : 0.0;
                })
                .sum();
            
            BigDecimal ventasHoy = BigDecimal.valueOf(ventasHoyDouble);
            
            // Comandas recientes (últimas 5)
            List<Comanda> comandasRecientes = comandaRepository.findAll().stream()
                .sorted((c1, c2) -> c2.getFecha().compareTo(c1.getFecha()))
                .limit(5)
                .collect(Collectors.toList());
            
            List<Map<String, Object>> comandasRecientesDTO = comandasRecientes.stream().map(c -> {
                Map<String, Object> comandaMap = new HashMap<>();
                comandaMap.put("id", c.getIdComanda());
                comandaMap.put("fecha", c.getFecha().toString());
                comandaMap.put("mesa", c.getMesa() != null ? c.getMesa().getIdMesa() : null);
                comandaMap.put("mesero", c.getMesero() != null ? c.getMesero().getNombre() : null);
                comandaMap.put("estado", c.getEstado() != null ? c.getEstado().getNombre() : null);
                Double total = c.calcularTotal();
                comandaMap.put("total", total != null ? total : 0.0);
                return comandaMap;
            }).collect(Collectors.toList());
            
            // Productos más vendidos (top 5)
            List<Object[]> productosMasVendidos = detalleComandaRepository.findProductosMasVendidos(null);
            List<Map<String, Object>> productosTopDTO = productosMasVendidos.stream()
                .limit(5)
                .map(result -> {
                    Producto producto = (Producto) result[0];
                    Long cantidadTotal = ((Number) result[1]).longValue();
                    Map<String, Object> productoMap = new HashMap<>();
                    productoMap.put("id", producto.getIdProducto());
                    productoMap.put("nombre", producto.getNombre());
                    productoMap.put("cantidadVendida", cantidadTotal);
                    productoMap.put("precio", producto.getPrecio() != null ? producto.getPrecio().doubleValue() : 0.0);
                    return productoMap;
                })
                .collect(Collectors.toList());
            
            // Resumen de comandas por estado
            Map<String, Long> comandasPorEstado = new HashMap<>();
            comandasPorEstado.put("Pendiente", (long) comandaRepository.findByNombreEstado("Pendiente").size());
            comandasPorEstado.put("En Preparacion", (long) comandaRepository.findByNombreEstado("En Preparacion").size());
            comandasPorEstado.put("Completada", (long) comandaRepository.findByNombreEstado("Completada").size());
            comandasPorEstado.put("Cancelada", (long) comandaRepository.findByNombreEstado("Cancelada").size());
            
            // Ventas de la semana
            LocalDateTime inicioSemana = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
            LocalDateTime finSemana = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            double ventasSemanaDouble = comandaRepository.findByFechaBetween(inicioSemana, finSemana).stream()
                .filter(c -> {
                    String nombreEstado = c.getEstado().getNombre();
                    return "Completado".equals(nombreEstado) || "Completada".equals(nombreEstado);
                })
                .mapToDouble(c -> {
                    Double total = c.calcularTotal();
                    return total != null ? total : 0.0;
                })
                .sum();
            
            BigDecimal ventasSemana = BigDecimal.valueOf(ventasSemanaDouble);
            
            // Total de productos
            long totalProductos = productoRepository.count();
            
            response.put("totalMesas", totalMesas);
            response.put("mesasOcupadas", mesasOcupadas);
            response.put("meserosActivos", meserosActivos);
            response.put("ordenesEnPreparacion", ordenesEnPreparacion);
            response.put("ventasHoy", ventasHoy.doubleValue());
            response.put("ventasSemana", ventasSemana.doubleValue());
            response.put("totalProductos", totalProductos);
            response.put("comandasRecientes", comandasRecientesDTO);
            response.put("productosMasVendidos", productosTopDTO);
            response.put("comandasPorEstado", comandasPorEstado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}


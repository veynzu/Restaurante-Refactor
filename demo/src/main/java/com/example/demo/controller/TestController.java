package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de prueba para verificar la conexión a la base de datos
 * y crear datos iniciales del sistema
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TelefonoRepository telefonoRepository;
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ComandaRepository comandaRepository;
    
    @Autowired
    private DetalleComandaRepository detalleComandaRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Endpoint para verificar la conexión a la base de datos
     */
    @GetMapping("/conexion")
    public ResponseEntity<Map<String, Object>> verificarConexion() {
        Map<String, Object> response = new HashMap<>();
        try {
            long countEstados = estadoRepository.count();
            long countRoles = rolRepository.count();
            long countCategorias = categoriaRepository.count();
            long countUsuarios = usuarioRepository.count();
            long countMesas = mesaRepository.count();
            long countProductos = productoRepository.count();
            long countComandas = comandaRepository.count();
            
            response.put("status", "success");
            response.put("message", "Conexión a la base de datos exitosa");
            response.put("data", Map.of(
                "estados", countEstados,
                "roles", countRoles,
                "categorias", countCategorias,
                "usuarios", countUsuarios,
                "mesas", countMesas,
                "productos", countProductos,
                "comandas", countComandas
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error de conexión: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint para crear datos iniciales del sistema
     */
    @PostMapping("/datos-iniciales")
    public ResponseEntity<Map<String, Object>> crearDatosIniciales() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. Crear Estados
            crearEstadosIniciales();
            
            // 2. Crear Roles
            crearRolesIniciales();
            
            // 3. Crear Categorías
            crearCategoriasIniciales();
            
            // 4. Crear Usuarios
            crearUsuariosIniciales();
            
            response.put("status", "success");
            response.put("message", "Datos básicos creados exitosamente");
            response.put("data", Map.of(
                "estados", estadoRepository.count(),
                "roles", rolRepository.count(),
                "categorias", categoriaRepository.count(),
                "usuarios", usuarioRepository.count()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al crear datos iniciales: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private void crearEstadosIniciales() {
        String[] nombresEstados = {"Disponible", "Ocupado", "Reservado", "Pendiente", "En Preparacion", "Completado", "Cancelado"};
        
        for (String nombre : nombresEstados) {
            if (!estadoRepository.existsByNombre(nombre)) {
                Estado estado = new Estado(nombre);
                estadoRepository.save(estado);
            }
        }
    }
    
    private void crearRolesIniciales() {
        String[] nombresRoles = {"Administrador", "Mesero", "Cocinero", "Cajero"};
        
        for (String nombre : nombresRoles) {
            if (!rolRepository.existsByNombre(nombre)) {
                Rol rol = new Rol(nombre);
                rolRepository.save(rol);
            }
        }
    }
    
    private void crearCategoriasIniciales() {
        String[] nombresCategorias = {"Entradas", "Platos Fuertes", "Bebidas", "Postres", "Ensaladas"};
        
        for (String nombre : nombresCategorias) {
            if (!categoriaRepository.existsByNombre(nombre)) {
                Categoria categoria = new Categoria(nombre);
                categoriaRepository.save(categoria);
            }
        }
    }
    
    private void crearUsuariosIniciales() {
        // Usuario Administrador
        if (!usuarioRepository.existsByIdUsuario("ADMIN001")) {
            Rol rolAdmin = rolRepository.findByNombre("Administrador").orElse(null);
            if (rolAdmin != null) {
                String passwordEncriptada = passwordEncoder.encode("admin123");
                Usuario admin = new Usuario("ADMIN001", "Administrador Principal", "admin@restaurante.com", passwordEncriptada, rolAdmin);
                usuarioRepository.save(admin);
            }
        }
        
        // Usuario Mesero
        if (!usuarioRepository.existsByIdUsuario("MESERO001")) {
            Rol rolMesero = rolRepository.findByNombre("Mesero").orElse(null);
            if (rolMesero != null) {
                String passwordEncriptada = passwordEncoder.encode("mesero123");
                Usuario mesero = new Usuario("MESERO001", "Juan Pérez", "mesero@restaurante.com", passwordEncriptada, rolMesero);
                usuarioRepository.save(mesero);
            }
        }
        
        // Usuario Cocinero
        if (!usuarioRepository.existsByIdUsuario("COCINERO001")) {
            Rol rolCocinero = rolRepository.findByNombre("Cocinero").orElse(null);
            if (rolCocinero != null) {
                String passwordEncriptada = passwordEncoder.encode("cocinero123");
                Usuario cocinero = new Usuario("COCINERO001", "María García", "cocinero@restaurante.com", passwordEncriptada, rolCocinero);
                usuarioRepository.save(cocinero);
            }
        }
    }
    
    private void crearMesasIniciales() {
        Estado estadoDisponible = estadoRepository.findByNombre("Disponible").orElse(null);
        if (estadoDisponible != null) {
            String[] ubicaciones = {"Terraza", "Salón Principal", "Salón VIP", "Terraza", "Salón Principal"};
            Integer[] capacidades = {2, 4, 6, 8, 4};
            
            for (int i = 0; i < ubicaciones.length; i++) {
                if (mesaRepository.count() < 10) { // Solo crear 10 mesas
                    Mesa mesa = new Mesa(capacidades[i], ubicaciones[i], estadoDisponible);
                    mesaRepository.save(mesa);
                }
            }
        }
    }
    
    private void crearProductosIniciales() {
        Categoria categoriaEntradas = categoriaRepository.findByNombre("Entradas").orElse(null);
        Categoria categoriaPlatosFuertes = categoriaRepository.findByNombre("Platos Fuertes").orElse(null);
        Categoria categoriaBebidas = categoriaRepository.findByNombre("Bebidas").orElse(null);
        
        if (categoriaEntradas != null && categoriaPlatosFuertes != null && categoriaBebidas != null) {
            // Entradas
            if (!productoRepository.existsByNombre("Ceviche de Pescado")) {
                Producto ceviche = new Producto("Ceviche de Pescado", new BigDecimal("25.00"), 50, categoriaEntradas);
                productoRepository.save(ceviche);
            }
            
            if (!productoRepository.existsByNombre("Anticuchos")) {
                Producto anticuchos = new Producto("Anticuchos", new BigDecimal("18.00"), 30, categoriaEntradas);
                productoRepository.save(anticuchos);
            }
            
            // Platos Fuertes
            if (!productoRepository.existsByNombre("Lomo Saltado")) {
                Producto lomoSaltado = new Producto("Lomo Saltado", new BigDecimal("35.00"), 40, categoriaPlatosFuertes);
                productoRepository.save(lomoSaltado);
            }
            
            if (!productoRepository.existsByNombre("Arroz con Pollo")) {
                Producto arrozConPollo = new Producto("Arroz con Pollo", new BigDecimal("28.00"), 35, categoriaPlatosFuertes);
                productoRepository.save(arrozConPollo);
            }
            
            // Bebidas
            if (!productoRepository.existsByNombre("Coca Cola")) {
                Producto cocaCola = new Producto("Coca Cola", new BigDecimal("8.00"), 100, categoriaBebidas);
                productoRepository.save(cocaCola);
            }
            
            if (!productoRepository.existsByNombre("Chicha Morada")) {
                Producto chichaMorada = new Producto("Chicha Morada", new BigDecimal("12.00"), 60, categoriaBebidas);
                productoRepository.save(chichaMorada);
            }
        }
    }
    
    /**
     * Endpoint para crear solo un estado de prueba
     */
    @PostMapping("/crear-estado")
    public ResponseEntity<Map<String, Object>> crearEstadoPrueba() {
        Map<String, Object> response = new HashMap<>();
        try {
            Estado estado = new Estado("Prueba");
            estadoRepository.save(estado);
            
            response.put("status", "success");
            response.put("message", "Estado creado exitosamente");
            response.put("estado", estado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al crear estado: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint para obtener todos los datos de prueba
     */
    @GetMapping("/datos")
    public ResponseEntity<Map<String, Object>> obtenerDatos() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("estados", estadoRepository.findAll());
            response.put("roles", rolRepository.findAll());
            response.put("categorias", categoriaRepository.findAll());
            response.put("usuarios", usuarioRepository.findAll());
            response.put("mesas", mesaRepository.findAll());
            response.put("productos", productoRepository.findAll());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al obtener datos: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

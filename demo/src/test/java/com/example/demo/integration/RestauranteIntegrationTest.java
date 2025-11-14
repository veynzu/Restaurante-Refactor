package com.example.demo.integration;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de Integración Completo
 * Prueba todo el flujo de la aplicación desde endpoints hasta base de datos
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RestauranteIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ComandaRepository comandaRepository;
    
    @Autowired
    private DetalleComandaRepository detalleComandaRepository;
    
    private Estado estadoDisponible;
    private Estado estadoPendiente;
    private Rol rolMesero;
    private Rol rolCocinero;
    private Categoria categoria;
    private Usuario mesero;
    private Usuario cocinero;
    private Mesa mesa;
    private Producto producto1;
    private Producto producto2;
    
    @BeforeEach
    void setUp() {
        // Limpiar base de datos
        detalleComandaRepository.deleteAll();
        comandaRepository.deleteAll();
        productoRepository.deleteAll();
        mesaRepository.deleteAll();
        usuarioRepository.deleteAll();
        categoriaRepository.deleteAll();
        rolRepository.deleteAll();
        estadoRepository.deleteAll();
        
        // Crear datos base
        estadoDisponible = estadoRepository.save(new Estado("DISPONIBLE"));
        estadoPendiente = estadoRepository.save(new Estado("PENDIENTE"));
        
        rolMesero = rolRepository.save(new Rol("MESERO"));
        rolCocinero = rolRepository.save(new Rol("COCINERO"));
        
        categoria = categoriaRepository.save(new Categoria("PLATOS FUERTES"));
        
        mesero = usuarioRepository.save(new Usuario("mesero001", "Maria Lopez", "mesero@test.com", "pass123", rolMesero));
        cocinero = usuarioRepository.save(new Usuario("cocinero001", "Carlos Rodriguez", "cocinero@test.com", "pass123", rolCocinero));
        
        mesa = mesaRepository.save(new Mesa(4, "Ventana", estadoDisponible));
        
        producto1 = productoRepository.save(new Producto("Lomo Saltado", new BigDecimal("25.00"), 50, categoria));
        producto2 = productoRepository.save(new Producto("Arroz Chaufa", new BigDecimal("18.00"), 30, categoria));
    }
    
    @Test
    void testFlujoCompletoDeComanda() throws Exception {
        // 1. Verificar que la base de datos tiene los datos iniciales
        assertThat(estadoRepository.count()).isGreaterThan(0);
        assertThat(rolRepository.count()).isGreaterThan(0);
        assertThat(usuarioRepository.count()).isGreaterThan(0);
        
        // 2. Crear una comanda
        Map<String, Object> comandaData = new HashMap<>();
        comandaData.put("mesa", Map.of("idMesa", mesa.getIdMesa()));
        comandaData.put("mesero", Map.of("idUsuario", mesero.getIdUsuario()));
        comandaData.put("estado", Map.of("idEstado", estadoPendiente.getIdEstado()));
        
        String comandaResponse = mockMvc.perform(post("/api/comandas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comandaData)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idComanda").exists())
            .andReturn().getResponse().getContentAsString();
        
        Map<String, Object> comanda = objectMapper.readValue(comandaResponse, Map.class);
        Integer idComanda = (Integer) comanda.get("idComanda");
        
        // 3. Agregar productos a la comanda
        Map<String, Object> detalle1 = new HashMap<>();
        detalle1.put("idComanda", idComanda);
        detalle1.put("idProducto", producto1.getIdProducto());
        detalle1.put("cantidad", 2);
        detalle1.put("precioUnitario", 25.00);
        
        mockMvc.perform(post("/api/detalle-comandas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalle1)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.cantidad").value(2))
            .andExpect(jsonPath("$.subtotal").value(50.00));
        
        // 4. Verificar que el stock se redujo
        Producto productoActualizado = productoRepository.findById(producto1.getIdProducto()).get();
        assertThat(productoActualizado.getStock()).isEqualTo(48); // 50 - 2
        
        // 5. Asignar cocinero
        mockMvc.perform(put("/api/comandas/" + idComanda + "/asignar-cocinero/cocinero001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cocinero.nombre").value("Carlos Rodriguez"));
        
        // 6. Verificar detalles de la comanda
        mockMvc.perform(get("/api/detalle-comandas/comanda/" + idComanda))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].producto.nombre").value("Lomo Saltado"));
        
        // 7. Calcular subtotal
        mockMvc.perform(get("/api/detalle-comandas/subtotal/" + idComanda))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subtotal").value(50.00));
    }
    
    @Test
    void testFlujoDeAutenticacion() throws Exception {
        // 1. Login exitoso
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "mesero@test.com");
        credentials.put("password", "pass123");
        
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Maria Lopez"))
            .andExpect(jsonPath("$.rol.nombre").value("MESERO"));
        
        // 2. Login con credenciales incorrectas
        credentials.put("password", "wrongpass");
        
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void testFlujoDeGestionDeMesas() throws Exception {
        // 1. Listar mesas disponibles
        mockMvc.perform(get("/api/mesas/disponibles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
        
        // 2. Ocupar mesa
        mockMvc.perform(put("/api/mesas/" + mesa.getIdMesa() + "/ocupar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado.nombre").value("OCUPADO"));
        
        // 3. Verificar que ya no está disponible
        long mesasDisponibles = mesaRepository.findMesasDisponibles().size();
        assertThat(mesasDisponibles).isEqualTo(0);
        
        // 4. Liberar mesa
        mockMvc.perform(put("/api/mesas/" + mesa.getIdMesa() + "/liberar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado.nombre").value("DISPONIBLE"));
    }
    
    @Test
    void testFlujoDeInventario() throws Exception {
        // 1. Verificar stock inicial
        Producto productoInicial = productoRepository.findById(producto1.getIdProducto()).get();
        int stockInicial = productoInicial.getStock();
        
        // 2. Reducir stock
        Map<String, Integer> request = new HashMap<>();
        request.put("cantidad", 10);
        
        mockMvc.perform(put("/api/productos/" + producto1.getIdProducto() + "/reducir-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock").value(stockInicial - 10));
        
        // 3. Aumentar stock
        request.put("cantidad", 20);
        
        mockMvc.perform(put("/api/productos/" + producto1.getIdProducto() + "/aumentar-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock").value(stockInicial - 10 + 20));
    }
    
    @Test
    void testValidacionDeStockEnDetalle() throws Exception {
        // Crear comanda
        Comanda comanda = comandaRepository.save(new Comanda(mesa, mesero, estadoPendiente));
        
        // Intentar agregar más productos que stock disponible
        Map<String, Object> detalle = new HashMap<>();
        detalle.put("idComanda", comanda.getIdComanda());
        detalle.put("idProducto", producto1.getIdProducto());
        detalle.put("cantidad", 1000); // Más que el stock
        detalle.put("precioUnitario", 25.00);
        
        mockMvc.perform(post("/api/detalle-comandas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalle)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("stock")));
    }
}

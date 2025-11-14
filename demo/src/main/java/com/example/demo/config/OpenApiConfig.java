package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API
 * Proporciona una interfaz interactiva para probar todos los endpoints
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Restaurante - Sistema de Gestión")
                .version("1.0.0")
                .description("""
                    # API REST para Sistema de Gestión de Restaurante
                    
                    Esta API proporciona endpoints completos para la gestión de un restaurante, incluyendo:
                    
                    ## Funcionalidades Principales:
                    - **Gestión de Usuarios:** Autenticación, roles (Administrador, Mesero, Cocinero)
                    - **Gestión de Mesas:** Estados, capacidad, ubicación, disponibilidad
                    - **Gestión de Productos:** Menú, categorías, precios, inventario
                    - **Gestión de Comandas:** Pedidos, asignación de meseros/cocineros, estados
                    - **Control de Inventario:** Reducción automática de stock, alertas
                    - **Cálculos Automáticos:** Subtotales, totales de comandas
                    
                    ## Tecnologías:
                    - Spring Boot 3.5.4
                    - Java 17
                    - MySQL 8.0
                    - JPA/Hibernate
                    
                    ## Principios Aplicados:
                    - Clean Code
                    - SOLID
                    - Arquitectura en Capas
                    - RESTful API
                    
                    ## Total de Endpoints: 120+
                    """))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor de Desarrollo")
            ));
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Aplica a todos los endpoints
                        .allowedOrigins("*")  // Permite cualquier origen
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false); // Si usas cookies o auth, márcalo true y quita "*"
            }
        };
    }
}


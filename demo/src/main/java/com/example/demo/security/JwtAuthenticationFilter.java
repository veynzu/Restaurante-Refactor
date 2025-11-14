package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro para interceptar todas las peticiones y validar el token JWT
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Saltar el filtro para endpoints públicos
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/swagger-ui.html") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/api/test/");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        System.out.println("🔍 JwtAuthenticationFilter - Procesando petición: " + request.getMethod() + " " + request.getRequestURI());
        
        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("🔍 Header Authorization presente: " + (authorizationHeader != null ? "Sí" : "No"));
        
        String userId = null;
        String jwt = null;
        
        // Extraer el token del header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("🔍 Token extraído (primeros 20 chars): " + (jwt.length() > 20 ? jwt.substring(0, 20) + "..." : jwt));
            try {
                userId = jwtUtil.extractUserId(jwt);
                System.out.println("🔑 Token extraído correctamente para usuario: " + userId + " en ruta: " + request.getRequestURI());
                logger.info("🔑 Token extraído correctamente para usuario: " + userId + " en ruta: " + request.getRequestURI());
            } catch (Exception e) {
                System.out.println("❌ Error al extraer userId del token JWT: " + e.getMessage());
                logger.error("❌ Error al extraer userId del token JWT en ruta " + request.getRequestURI() + ": " + e.getMessage(), e);
                // Si no se puede extraer el userId, el token es inválido
                // Responder con 401 Unauthorized
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token inválido o malformado: " + e.getMessage() + "\"}");
                return;
            }
        } else {
            // No hay token - Spring Security manejará el rechazo si el endpoint requiere autenticación
            System.out.println("⚠️ No se encontró header Authorization para: " + request.getRequestURI());
            logger.warn("⚠️ No se encontró header Authorization para: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        
        // Validar el token y establecer la autenticación
        if (userId != null) {
            try {
                System.out.println("🔍 Validando token para usuario: " + userId);
                
                // Verificar si el token ha expirado
                boolean tokenExpirado = jwtUtil.isTokenExpired(jwt);
                System.out.println("🔍 Token expirado: " + tokenExpirado);
                
                if (tokenExpirado) {
                    System.out.println("❌ Token EXPIRADO para usuario: " + userId);
                    logger.warn("❌ Token EXPIRADO para usuario: " + userId + " en ruta: " + request.getRequestURI());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\":\"Token expirado. Por favor, inicia sesión nuevamente.\"}");
                    return;
                }
                
                boolean tokenValido = jwtUtil.validateToken(jwt, userId);
                System.out.println("🔍 Token válido: " + tokenValido);
                
                if (tokenValido) {
                    String rol = jwtUtil.extractRol(jwt);
                    System.out.println("🔍 Rol extraído: " + rol);
                    
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userId, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
                    );
                    
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    System.out.println("✅ Autenticación establecida para usuario: " + userId + " con rol: " + rol);
                    logger.info("✅ Autenticación exitosa para usuario: " + userId + " con rol: " + rol + " en ruta: " + request.getRequestURI());
                } else {
                    System.out.println("❌ Token inválido (no coincide userId) para usuario: " + userId);
                    logger.warn("❌ Token inválido (no coincide userId) para usuario: " + userId + " en ruta: " + request.getRequestURI());
                    // Token inválido - Responder con 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\":\"Token inválido. El userId no coincide.\"}");
                    return;
                }
            } catch (Exception e) {
                System.out.println("❌ EXCEPCIÓN al validar token: " + e.getMessage());
                e.printStackTrace();
                logger.error("❌ Error al validar el token JWT en ruta " + request.getRequestURI() + ": " + e.getMessage(), e);
                // Error al validar - Responder con 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\":\"Error al validar el token: " + e.getMessage() + "\"}");
                return;
            }
        } else {
            System.out.println("⚠️ userId es null después de extraer el token");
            logger.warn("⚠️ userId es null después de extraer el token en ruta: " + request.getRequestURI());
        }
        
        System.out.println("➡️ Continuando con el filtro chain...");
        filterChain.doFilter(request, response);
    }
}


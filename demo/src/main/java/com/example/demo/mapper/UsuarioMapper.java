package com.example.demo.mapper;

import com.example.demo.dto.response.RolDTO;
import com.example.demo.dto.response.UsuarioResponseDTO;
import com.example.demo.dto.response.UsuarioSimpleDTO;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.UsuarioTelefono;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Usuario Entity y DTOs
 */
@Component
public class UsuarioMapper {
    
    /**
     * Convierte Usuario entity a UsuarioResponseDTO
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .fechaRegistro(usuario.getFechaRegistro())
                .rol(toRolDTO(usuario.getRol()))
                .telefonos(usuario.getUsuarioTelefonos() != null ? 
                    usuario.getUsuarioTelefonos().stream()
                        .map(ut -> ut.getTelefono().getNumero())
                        .collect(Collectors.toList()) 
                    : null)
                .build();
    }
    
    /**
     * Convierte Usuario entity a UsuarioSimpleDTO
     */
    public UsuarioSimpleDTO toSimpleDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return UsuarioSimpleDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().getNombre() : null)
                .build();
    }
    
    /**
     * Convierte Rol entity a RolDTO
     */
    private RolDTO toRolDTO(com.example.demo.entity.Rol rol) {
        if (rol == null) {
            return null;
        }
        
        return RolDTO.builder()
                .idRol(rol.getIdRol() != null ? rol.getIdRol().longValue() : null)
                .nombre(rol.getNombre())
                .descripcion(null) // Rol entity no tiene descripción
                .build();
    }
}


package com.example.demo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entidad que representa la relación Many-to-Many entre Usuario y Telefono
 * Basada en la tabla restaurante_usuarios_telefonos del MER
 */
@Entity
@Table(name = "restaurante_usuarios_telefonos")
public class UsuarioTelefono implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuarios_telefonos")
    private Integer idUsuariosTelefonos;
    
    // Relación Many-to-One con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    // Relación Many-to-One con Telefono
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_telefono", nullable = false)
    private Telefono telefono;
    
    // Constructores
    public UsuarioTelefono() {}
    
    public UsuarioTelefono(Usuario usuario, Telefono telefono) {
        this.usuario = usuario;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public Integer getIdUsuariosTelefonos() {
        return idUsuariosTelefonos;
    }
    
    public void setIdUsuariosTelefonos(Integer idUsuariosTelefonos) {
        this.idUsuariosTelefonos = idUsuariosTelefonos;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Telefono getTelefono() {
        return telefono;
    }
    
    public void setTelefono(Telefono telefono) {
        this.telefono = telefono;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioTelefono)) return false;
        
        UsuarioTelefono that = (UsuarioTelefono) o;
        
        if (usuario != null ? !usuario.equals(that.usuario) : that.usuario != null) return false;
        return telefono != null ? telefono.equals(that.telefono) : that.telefono == null;
    }
    
    @Override
    public int hashCode() {
        int result = usuario != null ? usuario.hashCode() : 0;
        result = 31 * result + (telefono != null ? telefono.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "UsuarioTelefono{" +
                "idUsuariosTelefonos=" + idUsuariosTelefonos +
                ", usuario=" + (usuario != null ? usuario.getIdUsuario() : null) +
                ", telefono=" + (telefono != null ? telefono.getNumero() : null) +
                '}';
    }
}

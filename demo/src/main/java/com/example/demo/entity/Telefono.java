package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa los números de teléfono
 * Basada en la tabla restaurante_telefonos del MER
 */
@Entity
@Table(name = "restaurante_telefonos")
public class Telefono {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefono")
    private Integer idTelefono;
    
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(max = 15, message = "El número de teléfono no puede exceder 15 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Formato de teléfono inválido")
    @Column(name = "numero", length = 15, nullable = false, unique = true)
    private String numero;
    
    // Relación Many-to-Many con Usuario a través de UsuarioTelefono
    @JsonIgnore
    @OneToMany(mappedBy = "telefono", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioTelefono> usuarioTelefonos = new ArrayList<>();
    
    // Constructores
    public Telefono() {}
    
    public Telefono(String numero) {
        this.numero = numero;
    }
    
    // Getters y Setters
    public Integer getIdTelefono() {
        return idTelefono;
    }
    
    public void setIdTelefono(Integer idTelefono) {
        this.idTelefono = idTelefono;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public List<UsuarioTelefono> getUsuarioTelefonos() {
        return usuarioTelefonos;
    }
    
    public void setUsuarioTelefonos(List<UsuarioTelefono> usuarioTelefonos) {
        this.usuarioTelefonos = usuarioTelefonos;
    }
    
    // Métodos de conveniencia
    public void agregarUsuarioTelefono(UsuarioTelefono usuarioTelefono) {
        usuarioTelefonos.add(usuarioTelefono);
        usuarioTelefono.setTelefono(this);
    }
    
    public void removerUsuarioTelefono(UsuarioTelefono usuarioTelefono) {
        usuarioTelefonos.remove(usuarioTelefono);
        usuarioTelefono.setTelefono(null);
    }
    
    @Override
    public String toString() {
        return "Telefono{" +
                "idTelefono=" + idTelefono +
                ", numero='" + numero + '\'' +
                '}';
    }
}

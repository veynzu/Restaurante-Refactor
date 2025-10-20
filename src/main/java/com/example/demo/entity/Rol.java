package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa los roles de los usuarios del sistema
 * Basada en la tabla restaurante_roles del MER
 */
@Entity
@Table(name = "restaurante_roles")
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;
    
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 20, message = "El nombre del rol no puede exceder 20 caracteres")
    @Column(name = "nombre", length = 20, nullable = false, unique = true)
    private String nombre;
    
    // Constructores
    public Rol() {}
    
    public Rol(String nombre) {
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public Integer getIdRol() {
        return idRol;
    }
    
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public String toString() {
        return "Rol{" +
                "idRol=" + idRol +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

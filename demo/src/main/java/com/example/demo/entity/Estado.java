package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa los estados del sistema (para mesas y comandas)
 * Basada en la tabla restaurante_estados del MER
 */
@Entity
@Table(name = "restaurante_estados")
public class Estado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado;
    
    @NotBlank(message = "El nombre del estado es obligatorio")
    @Size(max = 20, message = "El nombre del estado no puede exceder 20 caracteres")
    @Column(name = "nombre", length = 20, nullable = false)
    private String nombre;
    
    // Constructores
    public Estado() {}
    
    public Estado(String nombre) {
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public Integer getIdEstado() {
        return idEstado;
    }
    
    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public String toString() {
        return "Estado{" +
                "idEstado=" + idEstado +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

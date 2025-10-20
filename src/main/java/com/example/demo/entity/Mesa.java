package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa las mesas del restaurante
 * Basada en la tabla restaurante_mesas del MER
 */
@Entity
@Table(name = "restaurante_mesas")
public class Mesa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Integer idMesa;
    
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;
    
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 50, message = "La ubicación no puede exceder 50 caracteres")
    @Column(name = "ubicacion", length = 50, nullable = false)
    private String ubicacion;
    
    // Relación Many-to-One con Estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado", nullable = false)
    private Estado estado;
    
    // Relación One-to-Many con Comanda
    @JsonIgnore
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comanda> comandas = new ArrayList<>();
    
    // Constructores
    public Mesa() {}
    
    public Mesa(Integer capacidad, String ubicacion, Estado estado) {
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Integer getIdMesa() {
        return idMesa;
    }
    
    public void setIdMesa(Integer idMesa) {
        this.idMesa = idMesa;
    }
    
    public Integer getCapacidad() {
        return capacidad;
    }
    
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    public List<Comanda> getComandas() {
        return comandas;
    }
    
    public void setComandas(List<Comanda> comandas) {
        this.comandas = comandas;
    }
    
    // Métodos de conveniencia
    public void agregarComanda(Comanda comanda) {
        comandas.add(comanda);
        comanda.setMesa(this);
    }
    
    public void removerComanda(Comanda comanda) {
        comandas.remove(comanda);
        comanda.setMesa(null);
    }
    
    // Método para verificar si la mesa está disponible
    public boolean estaDisponible() {
        return estado != null && "Disponible".equals(estado.getNombre());
    }
    
    @Override
    public String toString() {
        return "Mesa{" +
                "idMesa=" + idMesa +
                ", capacidad=" + capacidad +
                ", ubicacion='" + ubicacion + '\'' +
                ", estado=" + estado +
                '}';
    }
}

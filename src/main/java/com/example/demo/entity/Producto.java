package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa los productos del menú del restaurante
 * Basada en la tabla restaurante_productos del MER
 */
@Entity
@Table(name = "restaurante_productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 50, message = "El nombre del producto no puede exceder 50 caracteres")
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;
    
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
    
    // Relación Many-to-One con Categoria
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria", nullable = false)
    private Categoria categoria;
    
    // Relación One-to-Many con DetalleComanda
    @JsonIgnore
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleComanda> detalleComandas = new ArrayList<>();
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombre, BigDecimal precio, Integer stock, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.estado = true;
    }
    
    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public List<DetalleComanda> getDetalleComandas() {
        return detalleComandas;
    }
    
    public void setDetalleComandas(List<DetalleComanda> detalleComandas) {
        this.detalleComandas = detalleComandas;
    }
    
    // Métodos de conveniencia
    public void agregarDetalleComanda(DetalleComanda detalleComanda) {
        detalleComandas.add(detalleComanda);
        detalleComanda.setProducto(this);
    }
    
    public void removerDetalleComanda(DetalleComanda detalleComanda) {
        detalleComandas.remove(detalleComanda);
        detalleComanda.setProducto(null);
    }
    
    // Método para verificar si el producto está disponible
    public boolean estaDisponible() {
        return estado && stock > 0;
    }
    
    // Método para reducir stock
    public void reducirStock(Integer cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }
    
    // Método para aumentar stock
    public void aumentarStock(Integer cantidad) {
        this.stock += cantidad;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", estado=" + estado +
                ", categoria=" + categoria +
                '}';
    }
}

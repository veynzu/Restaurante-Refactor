package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa las categorías de productos del restaurante
 * Basada en la tabla restaurante_categorias del MER
 */
@Entity
@Table(name = "restaurante_categorias")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 20, message = "El nombre de la categoría no puede exceder 20 caracteres")
    @Column(name = "nombre", length = 20, nullable = false, unique = true)
    private String nombre;
    
    // Relación One-to-Many con Producto
    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();
    
    // Constructores
    public Categoria() {}
    
    public Categoria(String nombre) {
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public Integer getIdCategoria() {
        return idCategoria;
    }
    
    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    // Métodos de conveniencia
    public void agregarProducto(Producto producto) {
        productos.add(producto);
        producto.setCategoria(this);
    }
    
    public void removerProducto(Producto producto) {
        productos.remove(producto);
        producto.setCategoria(null);
    }
    
    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

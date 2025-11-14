package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entidad que representa los detalles de una comanda (productos específicos)
 * Basada en la tabla restaurante_detalle_comanda del MER
 */
@Entity
@Table(name = "restaurante_detalle_comanda")
public class DetalleComanda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_comanda")
    private Integer idDetalleComanda;
    
    // Relación Many-to-One con Comanda
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_comanda", nullable = false)
    private Comanda comanda;
    
    // Relación Many-to-One con Producto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio unitario no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @NotNull(message = "El subtotal es obligatorio")
    @Min(value = 0, message = "El subtotal no puede ser negativo")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Relación Many-to-One con Estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;
    
    // Constructores
    public DetalleComanda() {}
    
    public DetalleComanda(Comanda comanda, Producto producto, Integer cantidad) {
        this.comanda = comanda;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    public DetalleComanda(Comanda comanda, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.comanda = comanda;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    // Getters y Setters
    public Integer getIdDetalleComanda() {
        return idDetalleComanda;
    }
    
    public void setIdDetalleComanda(Integer idDetalleComanda) {
        this.idDetalleComanda = idDetalleComanda;
    }
    
    public Comanda getComanda() {
        return comanda;
    }
    
    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    // Método para calcular subtotal
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
    
    // Método para recalcular subtotal (alias)
    public void recalcularSubtotal() {
        calcularSubtotal();
    }
    
    // Método para verificar disponibilidad de stock
    public boolean verificarStockDisponible() {
        return producto != null && producto.getStock() >= cantidad;
    }
    
    @Override
    public String toString() {
        return "DetalleComanda{" +
                "idDetalleComanda=" + idDetalleComanda +
                ", comanda=" + (comanda != null ? comanda.getIdComanda() : null) +
                ", producto=" + (producto != null ? producto.getNombre() : null) +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", estado=" + (estado != null ? estado.getNombre() : null) +
                '}';
    }
}

package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa las comandas (pedidos) del restaurante
 * Basada en la tabla restaurante_comandas del MER
 */
@Entity
@Table(name = "restaurante_comandas")
public class Comanda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comanda")
    private Integer idComanda;
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    // Relación Many-to-One con Mesa
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa", nullable = false)
    private Mesa mesa;
    
    // Relación Many-to-One con Usuario (Mesero)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mesero", nullable = false)
    private Usuario mesero;
    
    // Relación Many-to-One con Usuario (Cocinero)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cocinero")
    private Usuario cocinero;
    
    // Relación Many-to-One con Estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado", nullable = false)
    private Estado estado;
    
    // Relación One-to-Many con DetalleComanda
    @JsonIgnore
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleComanda> detalleComandas = new ArrayList<>();
    
    // Constructores
    public Comanda() {
        this.fecha = LocalDateTime.now();
    }
    
    public Comanda(Mesa mesa, Usuario mesero, Estado estado) {
        this();
        this.mesa = mesa;
        this.mesero = mesero;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Integer getIdComanda() {
        return idComanda;
    }
    
    public void setIdComanda(Integer idComanda) {
        this.idComanda = idComanda;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public Mesa getMesa() {
        return mesa;
    }
    
    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }
    
    public Usuario getMesero() {
        return mesero;
    }
    
    public void setMesero(Usuario mesero) {
        this.mesero = mesero;
    }
    
    public Usuario getCocinero() {
        return cocinero;
    }
    
    public void setCocinero(Usuario cocinero) {
        this.cocinero = cocinero;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
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
        detalleComanda.setComanda(this);
    }
    
    public void removerDetalleComanda(DetalleComanda detalleComanda) {
        detalleComandas.remove(detalleComanda);
        detalleComanda.setComanda(null);
    }
    
    // Método para calcular el total de la comanda
    public Double calcularTotal() {
        return detalleComandas.stream()
                .mapToDouble(detalle -> detalle.getSubtotal().doubleValue())
                .sum();
    }
    
    // Método para verificar si la comanda está pendiente
    public boolean estaPendiente() {
        return estado != null && "Pendiente".equals(estado.getNombre());
    }
    
    // Método para verificar si la comanda está completada
    public boolean estaCompletada() {
        return estado != null && "Completada".equals(estado.getNombre());
    }
    
    @Override
    public String toString() {
        return "Comanda{" +
                "idComanda=" + idComanda +
                ", fecha=" + fecha +
                ", mesa=" + mesa +
                ", mesero=" + mesero +
                ", cocinero=" + cocinero +
                ", estado=" + estado +
                '}';
    }
}

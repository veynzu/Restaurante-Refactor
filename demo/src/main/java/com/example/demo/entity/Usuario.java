package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa los usuarios del sistema (empleados, administradores, etc.)
 * Basada en la tabla restaurante_usuarios del MER
 */
@Entity
@Table(name = "restaurante_usuarios")
public class Usuario {
    
    @Id
    @Size(max = 20, message = "El ID del usuario no puede exceder 20 caracteres")
    @Column(name = "id_usuario", length = 20)
    private String idUsuario;
    
    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Size(max = 50, message = "El nombre del usuario no puede exceder 50 caracteres")
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Permitir escribir (recibir) pero no leer (enviar)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede exceder 255 caracteres")
    @Column(name = "password", length = 255, nullable = false)
    private String password;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    // Relación Many-to-One con Rol
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol", nullable = false)
    private Rol rol;
    
    // Relación One-to-Many con UsuarioTelefono
    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioTelefono> usuarioTelefonos = new ArrayList<>();
    
    // Relación One-to-Many con Comanda (como mesero)
    @JsonIgnore
    @OneToMany(mappedBy = "mesero", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comanda> comandasMesero = new ArrayList<>();
    
    // Relación One-to-Many con Comanda (como cocinero)
    @JsonIgnore
    @OneToMany(mappedBy = "cocinero", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comanda> comandasCocinero = new ArrayList<>();
    
    // Constructores
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public Usuario(String idUsuario, String nombre, String email, String password, Rol rol) {
        this();
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
    
    // Getters y Setters
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    public List<UsuarioTelefono> getUsuarioTelefonos() {
        return usuarioTelefonos;
    }
    
    public void setUsuarioTelefonos(List<UsuarioTelefono> usuarioTelefonos) {
        this.usuarioTelefonos = usuarioTelefonos;
    }
    
    public List<Comanda> getComandasMesero() {
        return comandasMesero;
    }
    
    public void setComandasMesero(List<Comanda> comandasMesero) {
        this.comandasMesero = comandasMesero;
    }
    
    public List<Comanda> getComandasCocinero() {
        return comandasCocinero;
    }
    
    public void setComandasCocinero(List<Comanda> comandasCocinero) {
        this.comandasCocinero = comandasCocinero;
    }
    
    // Métodos de conveniencia
    public void agregarTelefono(Telefono telefono) {
        UsuarioTelefono usuarioTelefono = new UsuarioTelefono(this, telefono);
        usuarioTelefonos.add(usuarioTelefono);
        telefono.getUsuarioTelefonos().add(usuarioTelefono);
    }
    
    public void removerTelefono(Telefono telefono) {
        UsuarioTelefono usuarioTelefono = new UsuarioTelefono(this, telefono);
        telefono.getUsuarioTelefonos().remove(usuarioTelefono);
        usuarioTelefonos.remove(usuarioTelefono);
        usuarioTelefono.setUsuario(null);
        usuarioTelefono.setTelefono(null);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", rol=" + rol +
                '}';
    }
}

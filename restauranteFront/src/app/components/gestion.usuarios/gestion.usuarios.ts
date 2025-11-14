import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService, Usuario, Rol } from '../../service/usuario.service';
import { AuthService } from '../../service/auth.service';

declare var bootstrap: any;

@Component({
  selector: 'app-gestion.usuarios',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion.usuarios.html',
  styleUrl: './gestion.usuarios.css',
})
export class GestionUsuarios implements OnInit {
  private usuarioService = inject(UsuarioService);
  private authService = inject(AuthService);

  usuarios: Usuario[] = [];
  roles: Rol[] = [];
  cargando = true;
  error: string | null = null;
  terminoBusqueda = '';

  // Usuario seleccionado para editar
  usuarioSeleccionado: Usuario | null = null;

  // Usuario a eliminar
  usuarioEliminar: Usuario | null = null;

  // Formulario de nuevo usuario
  nuevoUsuario = {
    nombre: '',
    email: '',
    password: '',
    rol: { idRol: 0, nombre: '' }
  };

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = null;

    // Cargar usuarios y roles en paralelo
    this.usuarioService.obtenerUsuarios().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);
        this.error = 'Error al cargar los usuarios';
        this.cargando = false;
      }
    });

    this.usuarioService.obtenerRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
      },
      error: (err) => {
        console.error('Error al cargar roles:', err);
      }
    });
  }

  buscarUsuarios(): void {
    if (!this.terminoBusqueda.trim()) {
      this.cargarDatos();
      return;
    }

    this.cargando = true;
    this.usuarioService.buscarUsuarios(this.terminoBusqueda).subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al buscar usuarios:', err);
        this.error = 'Error al buscar usuarios';
        this.cargando = false;
      }
    });
  }

  abrirModalCrear(): void {
    this.nuevoUsuario = {
      nombre: '',
      email: '',
      password: '',
      rol: { idRol: 0, nombre: '' }
    };
  }

  crearUsuario(): void {
    console.log('📝 Iniciando creación de usuario...');
    console.log('📋 Datos del formulario:', this.nuevoUsuario);
    console.log('📋 Roles disponibles:', this.roles);
    
    if (!this.validarFormulario(this.nuevoUsuario)) {
      return;
    }

    // El rol viene como número desde el formulario
    const idRolSeleccionado = Number(this.nuevoUsuario.rol.idRol);
    console.log('🔍 ID Rol seleccionado:', idRolSeleccionado);
    
    const rolSeleccionado = this.roles.find(r => r.idRol === idRolSeleccionado);
    console.log('🔍 Rol encontrado:', rolSeleccionado);
    
    if (!rolSeleccionado) {
      alert('⚠️ Debes seleccionar un rol válido');
      return;
    }

    const usuario: Usuario = {
      idUsuario: '', // El backend generará el ID automáticamente
      nombre: this.nuevoUsuario.nombre.trim(),
      email: this.nuevoUsuario.email.trim().toLowerCase(),
      password: this.nuevoUsuario.password,
      rol: rolSeleccionado
    };

    console.log('📦 Usuario a enviar al backend:', JSON.stringify(usuario, null, 2));
    console.log('🔑 Token disponible:', this.authService.getToken() ? 'Sí' : 'No');

    this.usuarioService.crearUsuario(usuario).subscribe({
      next: (usuarioCreado) => {
        console.log('✅ Usuario creado:', usuarioCreado);
        alert('Usuario creado exitosamente');
        this.cerrarModal('nuevoUsuarioModal');
        this.cargarDatos();
      },
      error: (err) => {
        console.error('❌ Error al crear usuario:', err);
        console.error('❌ Detalles del error:', {
          status: err.status,
          statusText: err.statusText,
          error: err.error,
          message: err.message
        });
        
        let mensaje = 'Error al crear el usuario';
        let necesitaLogin = false;
        
        if (err.status === 401 || err.status === 403) {
          // Verificar si el error viene del backend con un mensaje específico
          const errorBackend = err.error?.error || err.error?.message;
          if (errorBackend && (errorBackend.includes('expirado') || errorBackend.includes('Token'))) {
            mensaje = 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.';
          } else {
            mensaje = 'Error de autenticación. Por favor, inicia sesión nuevamente.';
          }
          necesitaLogin = true;
        } else if (err.error?.error) {
          mensaje = err.error.error;
        } else if (err.message) {
          mensaje = err.message;
        }
        
        // Mostrar el mensaje y luego redirigir si es necesario
        alert('Error: ' + mensaje);
        
        if (necesitaLogin) {
          // Esperar un momento para que el usuario vea el mensaje antes de redirigir
          setTimeout(() => {
            console.log('🔐 Cerrando sesión y redirigiendo al login...');
            this.authService.logout();
            window.location.href = '/login';
          }, 1500);
        }
      }
    });
  }

  abrirModalEditar(usuario: Usuario): void {
    // Crear una copia del usuario para editar
    this.usuarioSeleccionado = {
      ...usuario,
      rol: { ...usuario.rol }
    };
  }

  actualizarUsuario(): void {
    if (!this.usuarioSeleccionado) {
      return;
    }

    if (!this.validarFormularioEdicion(this.usuarioSeleccionado)) {
      return;
    }

    // No enviar password si está vacío (no se actualiza)
    const usuarioActualizado: any = {
      idUsuario: this.usuarioSeleccionado.idUsuario,
      nombre: this.usuarioSeleccionado.nombre.trim(),
      email: this.usuarioSeleccionado.email.trim().toLowerCase(),
      rol: this.usuarioSeleccionado.rol
    };

    // Solo agregar password si se proporcionó uno nuevo
    if (this.usuarioSeleccionado.password && this.usuarioSeleccionado.password.trim().length > 0) {
      usuarioActualizado.password = this.usuarioSeleccionado.password;
    }

    this.usuarioService.actualizarUsuario(this.usuarioSeleccionado.idUsuario, usuarioActualizado).subscribe({
      next: (usuarioActualizado) => {
        console.log('✅ Usuario actualizado:', usuarioActualizado);
        alert('Usuario actualizado exitosamente');
        this.cerrarModal('editarUsuarioModal');
        this.cargarDatos();
      },
      error: (err) => {
        console.error('❌ Error al actualizar usuario:', err);
        const mensaje = err.error?.error || 'Error al actualizar el usuario';
        alert('Error: ' + mensaje);
      }
    });
  }

  abrirModalEliminar(usuario: Usuario): void {
    this.usuarioEliminar = usuario;
  }

  eliminarUsuario(): void {
    if (!this.usuarioEliminar) {
      return;
    }

    if (!confirm(`¿Estás seguro de eliminar al usuario ${this.usuarioEliminar.nombre}?`)) {
      return;
    }

    this.usuarioService.eliminarUsuario(this.usuarioEliminar.idUsuario).subscribe({
      next: () => {
        console.log('✅ Usuario eliminado');
        alert('Usuario eliminado exitosamente');
        this.cerrarModal('eliminarUsuarioModal');
        this.cargarDatos();
      },
      error: (err) => {
        console.error('❌ Error al eliminar usuario:', err);
        const mensaje = err.error?.error || 'Error al eliminar el usuario';
        alert('Error: ' + mensaje);
      }
    });
  }

  compararRoles(r1: Rol | null, r2: Rol | null): boolean {
    return r1 && r2 ? r1.idRol === r2.idRol : r1 === r2;
  }

  formatearFecha(fecha: string | undefined): string {
    if (!fecha) return 'N/A';
    try {
      const date = new Date(fecha);
      return new Intl.DateTimeFormat('es-CO', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    } catch {
      return fecha;
    }
  }

  private validarFormulario(usuario: any): boolean {
    if (!usuario.nombre || usuario.nombre.trim().length === 0) {
      alert('⚠️ El nombre es obligatorio');
      return false;
    }
    if (!usuario.email || usuario.email.trim().length === 0) {
      alert('⚠️ El email es obligatorio');
      return false;
    }
    if (!usuario.password || usuario.password.length < 6) {
      alert('⚠️ La contraseña debe tener al menos 6 caracteres');
      return false;
    }
    if (!usuario.rol || usuario.rol.idRol === 0) {
      alert('⚠️ Debes seleccionar un rol');
      return false;
    }
    return true;
  }

  private validarFormularioEdicion(usuario: Usuario): boolean {
    if (!usuario.nombre || usuario.nombre.trim().length === 0) {
      alert('⚠️ El nombre es obligatorio');
      return false;
    }
    if (!usuario.email || usuario.email.trim().length === 0) {
      alert('⚠️ El email es obligatorio');
      return false;
    }
    if (usuario.password && usuario.password.length > 0 && usuario.password.length < 6) {
      alert('⚠️ La contraseña debe tener al menos 6 caracteres');
      return false;
    }
    if (!usuario.rol || !usuario.rol.idRol) {
      alert('⚠️ Debes seleccionar un rol');
      return false;
    }
    return true;
  }

  private cerrarModal(modalId: string): void {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }
}

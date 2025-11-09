import { Component, resource } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
declare var bootstrap: any;

@Component({
  selector: 'app-gestion.usuarios',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion.usuarios.html',
  styleUrl: './gestion.usuarios.css',
})
export class GestionUsuarios {
  token = '';
  constructor(private authService: AuthService) {
    const token = this.authService.getToken();
    this.token = token;
  }

  crearUsuario(nuevoUsuario: any) {
    console.log('📩 Datos recibidos del formulario:', nuevoUsuario);
    debugger;
    if (!nuevoUsuario.rol) {
      alert('⚠️ Debes seleccionar un rol antes de crear el usuario.');
      return;
    }
    debugger;
    const fechaActual = new Date().toISOString();
    const rolNombre = nuevoUsuario.rol;
    debugger;
    const rolesMap: Record<string, number> = {
      Administrador: 1,
      Mesero: 2,
      Cocinero: 3,
      Cajero: 4,
    };

    debugger;
    const usuarioFormateado = {
      idUsuario: nuevoUsuario.idUsuario,
      nombre: nuevoUsuario.nombre,
      email: nuevoUsuario.email,
      password: nuevoUsuario.password,
      //fechaRegistro: fechaActual,
      rol: {
        idRol: rolesMap[nuevoUsuario.rol] || 0,
        nombre: nuevoUsuario.rol,
      },
    };

    console.log('📦 Enviando usuario al backend:', usuarioFormateado);
    debugger;
    fetch('http://20.81.129.60:8080/api/usuarios', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this.token}`,
      },
      body: JSON.stringify(usuarioFormateado),
    })
      .then(async (res) => {
        if (!res.ok) {
          const error = await res.text();
          throw new Error(`Error al crear usuario: ${error}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log('✅ Usuario creado correctamente:', data);
        alert('Usuario creado con éxito');
        this.usuarios.reload(); // 🔁 Recargar lista de usuarios
      })
      .catch((err) => {
        console.error('❌ Error creando usuario:', err);
        alert('Ocurrió un error al crear el usuario');
      });
  }

  actualizarUsuario(usuario: any){}

  abrirModalEditar(usuario: any) {}

  abrirModalEliminar(usuario: any) {}

  onlogoff() {}

  usuarios = resource({
    loader: () => {
      debugger;
      return fetch('http://20.81.129.60:8080/api/usuarios', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.token}`, // <-- Aquí va el token JWT
        },
      }).then((result) => result.json());
    },
  });
}

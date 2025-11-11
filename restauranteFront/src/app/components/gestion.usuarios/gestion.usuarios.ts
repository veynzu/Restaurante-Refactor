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
  usuarioSeleccionado: any = {
    idUsuario: '',
    nombre: '',
    email: '',
    rol: {
      idRol: '',
      nombre: '',
    },
  };
  usuarioEliminar: any = {};
  token = '';
  constructor(private authService: AuthService) {
    const token = this.authService.getToken();
    this.token = token;
  }

  crearUsuario(nuevoUsuario: any) {
    console.log('📩 Datos recibidos del formulario:', nuevoUsuario);

    if (!nuevoUsuario.rol) {
      alert('⚠️ Debes seleccionar un rol antes de crear el usuario.');
      return;
    }
    const fechaActual = new Date().toISOString();
    const rolNombre = nuevoUsuario.rol;
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

    fetch('http://localhost:8080/api/usuarios', {
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

  actualizarUsuario(usuario: any) {}

  abrirModalEditar(usuario: any) {
    this.usuarioSeleccionado = { usuario };
  }

  abrirModalEliminar(usuario: any) {
    this.usuarioEliminar = { usuario };
  }

  eliminarUsuario(usuarioEliminar: any) {
    debugger;
    console.log('📩 Datos recibidos del formulario:', usuarioEliminar);
    const fechaActual = new Date().toISOString();
    let usuarioFormateado: any = {};
    if (!usuarioEliminar.rol) {
      alert('⚠️ Debes seleccionar un rol antes de crear el usuario.');
      return;
    } else {
      for (let r of this.roles.value()) {
        if (r.nombre == usuarioEliminar.rol.nombre) {
          debugger;
          usuarioFormateado = {
            idUsuario: usuarioEliminar.idUsuario,
            nombre: usuarioEliminar.nombre,
            email: usuarioEliminar.email,
            password: usuarioEliminar.password,
            //fechaRegistro: fechaActual,
            rol: {
              idRol: r.idRol,
              nombre: r.nombre,
            },
          };
        }
      }
    }

    console.log('📦 Enviando usuario al backend:', usuarioFormateado);

    fetch('http://localhost:8080/api/usuarios2', {
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
  compararRoles(r1: any, r2: any) {
    debugger;
    return r1 && r2 && r1.idRol === r2.idRol;
  }
  onlogoff() {}

  usuarios = resource({
    loader: () => {
      return fetch('http://localhost:8080/api/usuarios', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.token}`, // <-- Aquí va el token JWT
        },
      }).then((result) => result.json());
    },
  });

  roles = resource({
    loader: () => {
      return fetch('http://localhost:8080/api/roles', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.token}`, // <-- Aquí va el token JWT
        },
      }).then((result) => result.json());
    },
  });
}

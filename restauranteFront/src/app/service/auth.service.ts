import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private storageKey = 'usuario';

  setUsuario(usuario: any): void {
    localStorage.setItem(this.storageKey, JSON.stringify(usuario));
  }

  getUsuario(): any | null {
    const usuarioString = localStorage.getItem(this.storageKey);
    if (usuarioString) {
      try {
        return JSON.parse(usuarioString);
      } catch (error) {
        console.error('Error al parsear el usuario:', error);
      }
    }
    return null;
  }

  getToken(): string {
    const usuario = this.getUsuario();
    return usuario ? usuario.token : '';
  }

  getNombre(): string {
    const usuario = this.getUsuario();
    return usuario ? usuario.nombre : '';
  }

  getRol(): string {
    const usuario = this.getUsuario();
    return usuario ? usuario.rol : '';
  }

  getEmail(): string {
    const usuario = this.getUsuario();
    return usuario ? usuario.email : '';
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }
}

import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {
  nombreUsuario = '';
  rol = '';

  constructor(private authService: AuthService, private router: Router) {
    const usuario = this.authService.getUsuario();
    this.nombreUsuario = usuario?.nombre || '';
    this.rol = usuario?.rol || '';
  }

  onlogoff(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}

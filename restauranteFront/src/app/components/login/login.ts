import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { routes } from '../../app.routes';
import { Router, ROUTES } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  loginForm: FormGroup = new FormGroup({
    email: new FormControl(''),
    password: new FormControl(''),
  });

  http = inject(HttpClient);
  router = inject(Router);
  authService = inject(AuthService);

  onLogin() {
    const formValue = this.loginForm.value;
    console.log('🔐 Iniciando login con:', formValue.email);
    
    this.http.post('http://localhost:8080/api/auth/login', formValue).subscribe({
      next: (response: any) => {
        console.log('✅ Respuesta del login:', response);
        if (response.token) {
          console.log('🔑 Token recibido:', response.token.substring(0, 20) + '...');
          console.log('👤 Usuario:', response.nombre, 'Rol:', response.rol);
          alert('✅ Login exitoso');
          this.authService.setUsuario(response);
          
          // Verificar que se guardó correctamente
          const usuarioGuardado = this.authService.getUsuario();
          console.log('💾 Usuario guardado en localStorage:', usuarioGuardado);
          console.log('🔑 Token guardado:', this.authService.getToken() ? 'Sí' : 'No');
          
          this.router.navigateByUrl('/dashboard');
        } else {
          console.error('❌ No se recibió token en la respuesta');
          alert('❌ error en login' + JSON.stringify(response));
        }
      },
      error: (error) => {
        console.error('❌ Error en login:', error);
        alert(error.error?.error || 'Error al iniciar sesión');
      },
    });
  }
}

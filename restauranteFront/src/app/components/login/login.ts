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
    this.http.post('http://localhost:8080/api/auth/login', formValue).subscribe({
      next: (response: any) => {
        if (response.token) {
          alert('✅ Login exitoso');
          this.authService.setUsuario(response);
          this.router.navigateByUrl('/dashboard');
        } else {
          alert('❌ error en login' + JSON.stringify(response));
        }
      },
      error: (error) => {
        alert(error.error);
      },
    });
  }
}

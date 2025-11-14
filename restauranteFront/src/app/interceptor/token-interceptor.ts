import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { catchError, throwError } from 'rxjs';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  
  // Omitir el interceptor para peticiones de login
  if (req.url.includes('/api/auth/login')) {
    return next(req);
  }
  
  const token = auth.getToken();
  const usuario = auth.getUsuario();

  console.log('🔍 Interceptor - URL:', req.url);
  console.log('🔍 Interceptor - Usuario en localStorage:', usuario);
  console.log('🔍 Interceptor - Token disponible:', token ? 'Sí (' + token.substring(0, 20) + '...)' : 'No');

  if(token && token.trim() !== ''){
    const newReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    console.log('✅ Token enviado en petición a:', req.url);
    
    return next(newReq).pipe(
      catchError((error) => {
        // Si el error es 401 o 403, el token probablemente expiró
        // Pero solo redirigimos si NO es una petición al endpoint de login
        if ((error.status === 401 || error.status === 403) && !req.url.includes('/api/auth/login')) {
          console.warn('⚠️ Token expirado o inválido. El componente manejará la redirección.');
          // No hacemos logout ni redirigimos aquí, dejamos que el componente maneje el error
          // El componente puede mostrar el mensaje y luego redirigir si es necesario
        }
        return throwError(() => error);
      })
    );
  } else {
    console.warn('⚠️ No hay token disponible para la petición a:', req.url);
    console.warn('⚠️ Usuario completo:', usuario);
  }
  return next(req);
};

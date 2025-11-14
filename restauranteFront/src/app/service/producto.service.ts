import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface Categoria {
  idCategoria: number;
  nombre: string;
}

export interface Producto {
  idProducto: number;
  nombre: string;
  precio: number;
  stock: number;
  estado: boolean;
  categoria: Categoria | string | null; // Puede venir como objeto o string desde el backend
  idCategoria?: number; // ID de la categoría (viene del DTO)
}

export interface ProductoCreateRequest {
  nombre: string;
  precio: number;
  stock: number;
  idCategoria: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api';

  obtenerProductos(): Observable<Producto[]> {
    console.log('🌐 ========== PRODUCTO SERVICE - OBTENER PRODUCTOS ==========');
    console.log('🌐 URL:', `${this.apiUrl}/productos`);
    return this.http.get<Producto[]>(`${this.apiUrl}/productos`).pipe(
      tap({
        next: (productos) => {
          console.log('🌐 ✅ ========== PRODUCTOS RECIBIDOS EN SERVICE ==========');
          console.log('🌐 Service - Productos recibidos del backend (raw JSON):', JSON.stringify(productos, null, 2));
          console.log('🌐 Service - Tipo:', typeof productos);
          console.log('🌐 Service - Es Array:', Array.isArray(productos));
          if (productos && productos.length > 0) {
            console.log('🌐 Service - Cantidad:', productos.length);
            console.log('🌐 Service - Primer producto:', productos[0]);
            console.log('🌐 Service - Primer producto completo (JSON):', JSON.stringify(productos[0], null, 2));
            console.log('🌐 Service - Primer producto.categoria:', productos[0].categoria);
            console.log('🌐 Service - Primer producto.categoria tipo:', typeof productos[0].categoria);
            console.log('🌐 Service - Primer producto.idCategoria:', productos[0].idCategoria);
            console.log('🌐 Service - Primer producto.idCategoria tipo:', typeof productos[0].idCategoria);
            console.log('🌐 Service - Primer producto tiene idCategoria?:', 'idCategoria' in productos[0]);
            console.log('🌐 Service - Keys del primer producto:', Object.keys(productos[0]));
          } else {
            console.log('🌐 ⚠️ No hay productos o el array está vacío');
          }
        },
        error: (err) => {
          console.error('🌐 ❌ ========== ERROR EN SERVICE AL OBTENER PRODUCTOS ==========');
          console.error('🌐 Error:', err);
          console.error('🌐 Error completo:', JSON.stringify(err, null, 2));
        }
      })
    );
  }

  obtenerProductoPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/productos/${id}`);
  }

  obtenerProductosActivos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/productos/activos`);
  }

  crearProducto(producto: ProductoCreateRequest): Observable<Producto> {
    return this.http.post<Producto>(`${this.apiUrl}/productos/crear`, producto);
  }

  actualizarProducto(id: number, producto: Partial<Producto>): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/productos/${id}`, producto);
  }

  eliminarProducto(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/productos/${id}`);
  }

  activarProducto(id: number): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/productos/${id}/activar`, {});
  }

  desactivarProducto(id: number): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/productos/${id}/desactivar`, {});
  }

  actualizarStock(id: number, stock: number): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/productos/${id}/stock`, { stock });
  }
}


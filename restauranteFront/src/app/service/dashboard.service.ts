import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface ComandaReciente {
  id: number;
  fecha: string;
  mesa: number | null;
  mesero: string | null;
  estado: string | null;
  total: number;
}

export interface ProductoMasVendido {
  id: number;
  nombre: string;
  cantidadVendida: number;
  precio: number;
}

export interface DashboardEstadisticas {
  totalMesas: number;
  mesasOcupadas: number;
  meserosActivos: number;
  ordenesEnPreparacion: number;
  ventasHoy: number;
  ventasSemana: number;
  totalProductos: number;
  comandasRecientes: ComandaReciente[];
  productosMasVendidos: ProductoMasVendido[];
  comandasPorEstado: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api/dashboard';

  obtenerEstadisticas(): Observable<DashboardEstadisticas> {
    const token = this.authService.getToken();
    console.log('Token disponible:', token ? 'Sí' : 'No');
    if (!token) {
      console.warn('No hay token disponible. El usuario necesita hacer login.');
    }
    return this.http.get<DashboardEstadisticas>(`${this.apiUrl}/estadisticas`);
  }
}


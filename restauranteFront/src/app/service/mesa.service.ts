import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Estado {
  idEstado: number | null;
  nombre: string | null;
}

export interface Mesa {
  idMesa: number;
  capacidad: number;
  ubicacion: string;
  estado: Estado | null;
}

@Injectable({
  providedIn: 'root'
})
export class MesaService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api';

  // Obtener todas las mesas
  obtenerMesas(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/mesas`);
  }

  // Obtener mesa por ID
  obtenerMesaPorId(id: number): Observable<Mesa> {
    return this.http.get<Mesa>(`${this.apiUrl}/mesas/${id}`);
  }

  // Obtener mesas disponibles
  obtenerMesasDisponibles(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/mesas/disponibles`);
  }

  // Obtener mesas por estado
  obtenerMesasPorEstado(idEstado: number): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/mesas/estado/${idEstado}`);
  }

  // Buscar mesas por ubicación
  buscarMesasPorUbicacion(ubicacion: string): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/mesas/buscar`, {
      params: { ubicacion }
    });
  }

  // Buscar mesas por capacidad mínima
  buscarMesasPorCapacidad(capacidadMinima: number): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/mesas/capacidad/${capacidadMinima}`);
  }

  // Obtener mesas disponibles por capacidad
  obtenerMesasDisponiblesPorCapacidad(capacidad: number): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/mesas/disponibles/capacidad/${capacidad}`);
  }

  // Crear nueva mesa
  crearMesa(mesa: Partial<Mesa>): Observable<Mesa> {
    return this.http.post<Mesa>(`${this.apiUrl}/mesas`, mesa);
  }

  // Crear mesa con datos básicos
  crearMesaConDatos(capacidad: number, ubicacion: string, estado: string): Observable<Mesa> {
    return this.http.post<Mesa>(`${this.apiUrl}/mesas/crear`, {
      capacidad,
      ubicacion,
      estado
    });
  }

  // Actualizar mesa
  actualizarMesa(id: number, mesa: Partial<Mesa>): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/mesas/${id}`, mesa);
  }

  // Eliminar mesa
  eliminarMesa(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/mesas/${id}`);
  }

  // Cambiar estado de mesa
  cambiarEstadoMesa(id: number, idEstado: number): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/mesas/${id}/estado/${idEstado}`, {});
  }

  // Ocupar mesa
  ocuparMesa(id: number): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/mesas/${id}/ocupar`, {});
  }

  // Liberar mesa
  liberarMesa(id: number): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/mesas/${id}/liberar`, {});
  }

  // Reservar mesa
  reservarMesa(id: number): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/mesas/${id}/reservar`, {});
  }

  // Obtener todos los estados
  obtenerEstados(): Observable<Estado[]> {
    return this.http.get<Estado[]>(`${this.apiUrl}/estados`);
  }

  // Contar mesas
  contarMesas(): Observable<{ total: number }> {
    return this.http.get<{ total: number }>(`${this.apiUrl}/mesas/count`);
  }

  // Contar mesas por estado
  contarMesasPorEstado(idEstado: number): Observable<{ total: number }> {
    return this.http.get<{ total: number }>(`${this.apiUrl}/mesas/count/estado/${idEstado}`);
  }
}


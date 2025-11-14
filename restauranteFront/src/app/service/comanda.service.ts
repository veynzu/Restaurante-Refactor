import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Estado {
  idEstado: number | null;
  nombre: string | null;
  descripcion?: string | null;
}

export interface MesaSimple {
  idMesa: number;
  ubicacion: string;
  capacidad: number;
  estado: string | null;
}

export interface UsuarioSimple {
  idUsuario: string;
  nombre: string;
  email: string;
  rol: string | null;
}

export interface DetalleComanda {
  idDetalle: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  estado: string | null;
}

export interface Comanda {
  idComanda: number;
  fecha: string;
  mesa: MesaSimple | null;
  mesero: UsuarioSimple | null;
  cocinero: UsuarioSimple | null;
  estado: Estado | null;
  productos: DetalleComanda[] | null;
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class ComandaService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api';

  // Obtener todas las comandas
  obtenerComandas(): Observable<Comanda[]> {
    return this.http.get<Comanda[]>(`${this.apiUrl}/comandas`);
  }

  // Obtener comanda por ID
  obtenerComandaPorId(id: number): Observable<Comanda> {
    return this.http.get<Comanda>(`${this.apiUrl}/comandas/${id}`);
  }

  // Obtener comandas pendientes
  obtenerComandasPendientes(): Observable<Comanda[]> {
    return this.http.get<Comanda[]>(`${this.apiUrl}/comandas/pendientes`);
  }

  // Obtener comandas en preparación
  obtenerComandasEnPreparacion(): Observable<Comanda[]> {
    return this.http.get<Comanda[]>(`${this.apiUrl}/comandas/preparacion`);
  }

  // Obtener comandas por estado
  obtenerComandasPorEstado(idEstado: number): Observable<Comanda[]> {
    return this.http.get<Comanda[]>(`${this.apiUrl}/comandas/estado/${idEstado}`);
  }

  // Cambiar estado de comanda
  cambiarEstadoComanda(id: number, idEstado: number): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${id}/estado/${idEstado}`, {});
  }

  // Marcar como pendiente
  marcarComoPendiente(id: number): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${id}/pendiente`, {});
  }

  // Marcar como en preparación (con cocinero)
  marcarComoEnPreparacion(id: number, idCocinero: string): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${id}/preparacion/${idCocinero}`, {});
  }

  // Marcar como completada
  marcarComoCompletada(id: number): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${id}/completada`, {});
  }

  // Marcar como cancelada
  marcarComoCancelada(id: number): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${id}/cancelada`, {});
  }

  // Asignar cocinero
  asignarCocinero(id: number, idCocinero: string): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${id}/asignar-cocinero/${idCocinero}`, {});
  }

  // Obtener comandas por mesa
  obtenerComandasPorMesa(idMesa: number): Observable<Comanda[]> {
    return this.http.get<Comanda[]>(`${this.apiUrl}/comandas/mesa/${idMesa}`);
  }

  // Obtener facturación de una mesa
  obtenerFacturacionMesa(idMesa: number): Observable<FacturacionMesa> {
    return this.http.get<FacturacionMesa>(`${this.apiUrl}/comandas/mesa/${idMesa}/facturacion`);
  }

  // Verificar si todas las comandas están completadas
  verificarComandasCompletadas(idMesa: number): Observable<{ todasCompletadas: boolean }> {
    return this.http.get<{ todasCompletadas: boolean }>(`${this.apiUrl}/comandas/mesa/${idMesa}/verificar-completadas`);
  }

  // Finalizar todas las comandas de una mesa y liberarla
  finalizarYLiberarMesa(idMesa: number): Observable<{
    message: string;
    comandasFinalizadas: number;
    mesaLiberada: boolean;
    mesa?: {
      idMesa: number;
      ubicacion: string;
      estado: string;
    };
  }> {
    return this.http.post<{
      message: string;
      comandasFinalizadas: number;
      mesaLiberada: boolean;
      mesa?: {
        idMesa: number;
        ubicacion: string;
        estado: string;
      };
    }>(`${this.apiUrl}/comandas/mesa/${idMesa}/finalizar-y-liberar`, {});
  }

  // Marcar una comanda como pagada
  marcarComandaComoPagada(idComanda: number): Observable<Comanda> {
    return this.http.put<Comanda>(`${this.apiUrl}/comandas/${idComanda}/pagar`, {});
  }

  // Marcar todas las comandas completadas de una mesa como pagadas
  marcarTodasComandasPagadas(idMesa: number): Observable<{
    message: string;
    comandasPagadas: number;
  }> {
    return this.http.post<{
      message: string;
      comandasPagadas: number;
    }>(`${this.apiUrl}/comandas/mesa/${idMesa}/pagar-todas`, {});
  }
}

export interface ComandaFacturacion {
  idComanda: number;
  fecha: string;
  estado: string;
  mesero: string;
  cocinero: string;
  total: number;
  cantidadProductos: number;
  pagada: boolean;
}

export interface FacturacionMesa {
  idMesa: number;
  ubicacionMesa: string;
  totalComandas: number;
  comandasCompletadas: number;
  comandasPendientes: number;
  comandasPagadas: number;
  todasCompletadas: boolean;
  todasPagadas: boolean;
  totalAPagar: number;
  comandas: ComandaFacturacion[];
}


import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MesaService, Mesa, Estado } from '../../service/mesa.service';
import { AuthService } from '../../service/auth.service';
import { ComandaService, FacturacionMesa, ComandaFacturacion } from '../../service/comanda.service';

declare var bootstrap: any;

@Component({
  selector: 'app-gestion.mesas',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion.mesas.html',
  styleUrl: './gestion.mesas.css',
})
export class GestionMesas implements OnInit {
  private mesaService = inject(MesaService);
  private authService = inject(AuthService);
  private comandaService = inject(ComandaService);

  mesas: Mesa[] = [];
  estados: Estado[] = [];
  estadosMesas: Estado[] = []; // Solo estados relevantes para mesas
  mesasFiltradas: Mesa[] = [];
  
  // Estados permitidos para mesas (comparación case-insensitive)
  // En la DB: Disponible, Ocupado, Reservado
  private readonly ESTADOS_MESAS_PERMITIDOS = ['disponible', 'ocupado', 'reservado'];
  cargando = true;
  error: string | null = null;

  // Filtros
  filtroEstado: number | null = null;
  terminoBusqueda = '';
  capacidadMinima: number | null = null;

  // Estadísticas
  estadisticas = {
    total: 0,
    disponibles: 0,
    ocupadas: 0,
    reservadas: 0
  };

  // Modal
  mesaSeleccionada: Mesa | null = null;
  mesaEliminar: Mesa | null = null;
  nuevoMesa = {
    capacidad: 1,
    ubicacion: '',
    estado: { idEstado: 0, nombre: '' }
  };

  // Facturación
  facturacionMesa: FacturacionMesa | null = null;
  cargandoFacturacion = false;

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = null;

    // Verificar autenticación antes de cargar datos
    const token = this.authService.getToken();
    const usuario = this.authService.getUsuario();
    console.log('🔐 Verificando autenticación antes de cargar mesas...');
    console.log('🔐 Token disponible:', token ? 'Sí' : 'No');
    console.log('🔐 Usuario en localStorage:', usuario);

    if (!token || token.trim() === '') {
      console.error('❌ No hay token disponible. Redirigiendo al login...');
      this.error = 'Sesión expirada. Por favor, inicia sesión nuevamente.';
      this.cargando = false;
      setTimeout(() => {
        this.authService.logout();
        window.location.href = '/login';
      }, 2000);
      return;
    }

    // Cargar mesas y estados en paralelo
    this.mesaService.obtenerMesas().subscribe({
      next: (mesas) => {
        this.mesas = mesas;
        this.aplicarFiltros();
        this.calcularEstadisticas();
        this.cargando = false;
      },
      error: (err) => {
        console.error('❌ Error al cargar mesas:', err);
        console.error('❌ Status:', err.status);
        console.error('❌ Error completo:', err);
        if (err.status === 401 || err.status === 403) {
          this.error = 'Sesión expirada. Por favor, inicia sesión nuevamente.';
          setTimeout(() => {
            this.authService.logout();
            window.location.href = '/login';
          }, 2000);
        } else {
          this.error = 'Error al cargar las mesas';
        }
        this.cargando = false;
      }
    });

    this.mesaService.obtenerEstados().subscribe({
      next: (estados) => {
        this.estados = estados;
        // Filtrar solo los estados relevantes para mesas (comparación sin importar mayúsculas/minúsculas)
        this.estadosMesas = estados.filter(estado => {
          if (!estado || !estado.nombre) return false;
          const nombreLower = estado.nombre.toLowerCase().trim();
          return this.ESTADOS_MESAS_PERMITIDOS.includes(nombreLower);
        });
        console.log('🔍 Estados cargados del backend:', estados);
        console.log('✅ Estados filtrados para mesas:', this.estadosMesas);
        console.log('📋 Nombres de estados filtrados:', this.estadosMesas.map(e => e.nombre));
      },
      error: (err) => {
        console.error('❌ Error al cargar estados:', err);
        console.error('❌ Status:', err.status);
        if (err.status === 401 || err.status === 403) {
          // Si es error de autenticación, ya se manejará en el error de mesas
        }
        this.estadosMesas = []; // Asegurar que esté vacío si hay error
      }
    });
  }

  calcularEstadisticas(): void {
    this.estadisticas.total = this.mesas.length;
    this.estadisticas.disponibles = this.mesas.filter(m => 
      m.estado?.nombre?.toLowerCase() === 'disponible'
    ).length;
    this.estadisticas.ocupadas = this.mesas.filter(m => 
      m.estado?.nombre?.toLowerCase() === 'ocupado'
    ).length;
    this.estadisticas.reservadas = this.mesas.filter(m => 
      m.estado?.nombre?.toLowerCase() === 'reservado'
    ).length;
  }

  aplicarFiltros(): void {
    let resultado = [...this.mesas];

    // Filtro por estado
    if (this.filtroEstado) {
      resultado = resultado.filter(m => m.estado?.idEstado === this.filtroEstado);
    }

    // Filtro por búsqueda (ubicación)
    if (this.terminoBusqueda.trim()) {
      const busqueda = this.terminoBusqueda.toLowerCase().trim();
      resultado = resultado.filter(m => 
        m.ubicacion?.toLowerCase().includes(busqueda) ||
        m.idMesa.toString().includes(busqueda)
      );
    }

    // Filtro por capacidad mínima
    if (this.capacidadMinima && this.capacidadMinima > 0) {
      resultado = resultado.filter(m => m.capacidad >= this.capacidadMinima!);
    }

    this.mesasFiltradas = resultado;
  }

  limpiarFiltros(): void {
    this.filtroEstado = null;
    this.terminoBusqueda = '';
    this.capacidadMinima = null;
    this.aplicarFiltros();
  }

  obtenerColorEstado(estado: string | null | undefined): string {
    if (!estado) return 'secondary';
    const estadoLower = estado.toLowerCase();
    switch (estadoLower) {
      case 'disponible':
        return 'success';
      case 'ocupado':
        return 'danger';
      case 'reservado':
        return 'warning';
      default:
        return 'secondary';
    }
  }

  obtenerIconoEstado(estado: string | null | undefined): string {
    if (!estado) return '❓';
    const estadoLower = estado.toLowerCase();
    switch (estadoLower) {
      case 'disponible':
        return '✅';
      case 'ocupado':
        return '🔴';
      case 'reservado':
        return '🟡';
      default:
        return '❓';
    }
  }

  // Acciones rápidas
  ocuparMesa(mesa: Mesa): void {
    if (confirm(`¿Deseas ocupar la mesa ${mesa.idMesa} (${mesa.ubicacion})?`)) {
      this.mesaService.ocuparMesa(mesa.idMesa).subscribe({
        next: () => {
          alert('Mesa ocupada exitosamente');
          this.cargarDatos();
        },
        error: (err) => {
          console.error('Error al ocupar mesa:', err);
          alert('Error al ocupar la mesa: ' + (err.error?.error || err.message));
        }
      });
    }
  }

  liberarMesa(mesa: Mesa): void {
    if (confirm(`¿Deseas liberar la mesa ${mesa.idMesa} (${mesa.ubicacion})?`)) {
      this.mesaService.liberarMesa(mesa.idMesa).subscribe({
        next: () => {
          alert('Mesa liberada exitosamente');
          this.cargarDatos();
        },
        error: (err) => {
          console.error('Error al liberar mesa:', err);
          alert('Error al liberar la mesa: ' + (err.error?.error || err.message));
        }
      });
    }
  }

  reservarMesa(mesa: Mesa): void {
    if (confirm(`¿Deseas reservar la mesa ${mesa.idMesa} (${mesa.ubicacion})?`)) {
      this.mesaService.reservarMesa(mesa.idMesa).subscribe({
        next: () => {
          alert('Mesa reservada exitosamente');
          this.cargarDatos();
        },
        error: (err) => {
          console.error('Error al reservar mesa:', err);
          alert('Error al reservar la mesa: ' + (err.error?.error || err.message));
        }
      });
    }
  }

  // Verificar si la mesa está en un estado específico
  esEstado(mesa: Mesa, estadoNombre: string): boolean {
    return mesa.estado?.nombre?.toLowerCase() === estadoNombre.toLowerCase();
  }

  // CRUD
  crearMesa(): void {
    console.log('📝 ========== INICIANDO CREACIÓN DE MESA ==========');
    console.log('📋 nuevoMesa completo:', JSON.stringify(this.nuevoMesa, null, 2));
    console.log('📋 nuevoMesa.estado:', this.nuevoMesa.estado);
    console.log('📋 nuevoMesa.estado.idEstado:', this.nuevoMesa.estado?.idEstado);
    console.log('📋 estadosMesas:', this.estadosMesas);
    
    if (!this.validarFormulario(this.nuevoMesa)) {
      return;
    }

    // Verificar que el estado esté correctamente asignado
    if (!this.nuevoMesa.estado || !this.nuevoMesa.estado.idEstado || this.nuevoMesa.estado.idEstado === 0) {
      alert('⚠️ Debes seleccionar un estado válido para mesas (Disponible, Ocupado o Reservado)');
      console.error('❌ Estado inválido:', this.nuevoMesa.estado);
      return;
    }

    const estadoSeleccionado = this.estadosMesas.find(e => e.idEstado === this.nuevoMesa.estado.idEstado);
    if (!estadoSeleccionado) {
      alert('⚠️ Debes seleccionar un estado válido para mesas (Disponible, Ocupado o Reservado)');
      console.error('❌ Estado no encontrado en estadosMesas con id:', this.nuevoMesa.estado.idEstado);
      console.error('📋 estadosMesas disponibles:', this.estadosMesas);
      return;
    }
    
    console.log('✅ Estado seleccionado encontrado:', estadoSeleccionado);

    const mesa: Partial<Mesa> = {
      capacidad: this.nuevoMesa.capacidad,
      ubicacion: this.nuevoMesa.ubicacion.trim(),
      estado: estadoSeleccionado
    };

    this.mesaService.crearMesa(mesa).subscribe({
      next: (mesaCreada) => {
        alert('Mesa creada exitosamente');
        this.cerrarModal('nuevoMesaModal');
        this.limpiarFormulario();
        this.cargarDatos();
      },
      error: (err) => {
        console.error('Error al crear mesa:', err);
        alert('Error al crear la mesa: ' + (err.error?.error || err.message));
      }
    });
  }

  actualizarMesa(): void {
    if (!this.mesaSeleccionada) {
      alert('⚠️ No hay mesa seleccionada');
      return;
    }

    if (!this.validarFormularioEdicion(this.mesaSeleccionada)) {
      return;
    }

    if (!this.mesaSeleccionada.estado || !this.mesaSeleccionada.estado.idEstado) {
      alert('⚠️ Debes seleccionar un estado válido para mesas (Disponible, Ocupado o Reservado)');
      return;
    }

    // Guardar el idEstado en una variable local para evitar problemas de null
    const idEstadoSeleccionado = this.mesaSeleccionada.estado.idEstado;
    const estadoSeleccionado = this.estadosMesas.find(e => e.idEstado === idEstadoSeleccionado);
    
    if (!estadoSeleccionado) {
      alert('⚠️ Debes seleccionar un estado válido para mesas (Disponible, Ocupado o Reservado)');
      return;
    }

    const mesaActualizada: Partial<Mesa> = {
      capacidad: this.mesaSeleccionada.capacidad,
      ubicacion: this.mesaSeleccionada.ubicacion.trim(),
      estado: estadoSeleccionado
    };

    this.mesaService.actualizarMesa(this.mesaSeleccionada.idMesa, mesaActualizada).subscribe({
      next: () => {
        alert('Mesa actualizada exitosamente');
        this.cerrarModal('editarMesaModal');
        this.cargarDatos();
      },
      error: (err) => {
        console.error('Error al actualizar mesa:', err);
        alert('Error al actualizar la mesa: ' + (err.error?.error || err.message));
      }
    });
  }

  eliminarMesa(): void {
    if (!this.mesaEliminar) return;

    this.mesaService.eliminarMesa(this.mesaEliminar.idMesa).subscribe({
      next: () => {
        alert('Mesa eliminada exitosamente');
        this.cerrarModal('eliminarMesaModal');
        this.cargarDatos();
      },
      error: (err) => {
        console.error('Error al eliminar mesa:', err);
        alert('Error al eliminar la mesa: ' + (err.error?.error || err.message));
      }
    });
  }

  // Modales
  abrirModalNuevo(): void {
    this.limpiarFormulario();
    const modal = new bootstrap.Modal(document.getElementById('nuevoMesaModal'));
    modal.show();
  }

  abrirModalEditar(mesa: Mesa): void {
    this.mesaSeleccionada = { 
      ...mesa,
      estado: mesa.estado ? { ...mesa.estado } : null
    };
    const modal = new bootstrap.Modal(document.getElementById('editarMesaModal'));
    modal.show();
  }

  cambiarEstadoMesaSeleccionada(idEstado: number): void {
    if (!this.mesaSeleccionada) return;
    const estadoEncontrado = this.estadosMesas.find(e => e.idEstado === idEstado);
    if (estadoEncontrado) {
      this.mesaSeleccionada.estado = { ...estadoEncontrado };
    }
  }

  cambiarEstadoNuevoMesa(idEstado: number | string | null): void {
    console.log('🔄 cambiarEstadoNuevoMesa llamado con:', idEstado, 'tipo:', typeof idEstado);
    
    if (!idEstado || idEstado === '0' || idEstado === 0) {
      console.warn('⚠️ ID de estado inválido:', idEstado);
      this.nuevoMesa.estado = { idEstado: 0, nombre: '' };
      return;
    }
    
    // Convertir a número si viene como string
    const idEstadoNum = typeof idEstado === 'string' ? parseInt(idEstado, 10) : idEstado;
    
    console.log('🔄 idEstado convertido a número:', idEstadoNum);
    console.log('📋 estadosMesas disponibles:', this.estadosMesas);
    console.log('📋 IDs en estadosMesas:', this.estadosMesas.map(e => e.idEstado));
    
    const estadoEncontrado = this.estadosMesas.find(e => e.idEstado === idEstadoNum);
    if (estadoEncontrado && estadoEncontrado.idEstado !== null && estadoEncontrado.nombre !== null) {
      // Asegurar que los valores no sean null antes de asignar
      this.nuevoMesa.estado = { 
        idEstado: estadoEncontrado.idEstado, 
        nombre: estadoEncontrado.nombre 
      };
      console.log('✅ Estado actualizado en nuevoMesa:', this.nuevoMesa.estado);
      console.log('✅ nuevoMesa completo después del cambio:', JSON.stringify(this.nuevoMesa, null, 2));
    } else {
      console.error('❌ Estado no encontrado con id:', idEstadoNum);
      console.error('❌ IDs disponibles en estadosMesas:', this.estadosMesas.map(e => e.idEstado));
      this.nuevoMesa.estado = { idEstado: 0, nombre: '' };
    }
  }

  abrirModalEliminar(mesa: Mesa): void {
    this.mesaEliminar = mesa;
    const modal = new bootstrap.Modal(document.getElementById('eliminarMesaModal'));
    modal.show();
  }

  cerrarModal(modalId: string): void {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }

  limpiarFormulario(): void {
    this.nuevoMesa = {
      capacidad: 1,
      ubicacion: '',
      estado: { idEstado: 0, nombre: '' }
    };
  }

  validarFormulario(mesa: any): boolean {
    if (!mesa.ubicacion || mesa.ubicacion.trim().length === 0) {
      alert('⚠️ La ubicación es obligatoria');
      return false;
    }
    if (!mesa.capacidad || mesa.capacidad < 1) {
      alert('⚠️ La capacidad debe ser al menos 1');
      return false;
    }
    if (!mesa.estado || !mesa.estado.idEstado || mesa.estado.idEstado === 0) {
      alert('⚠️ Debes seleccionar un estado');
      return false;
    }
    return true;
  }

  validarFormularioEdicion(mesa: Mesa): boolean {
    if (!mesa.ubicacion || mesa.ubicacion.trim().length === 0) {
      alert('⚠️ La ubicación es obligatoria');
      return false;
    }
    if (!mesa.capacidad || mesa.capacidad < 1) {
      alert('⚠️ La capacidad debe ser al menos 1');
      return false;
    }
    if (!mesa.estado || !mesa.estado.idEstado) {
      alert('⚠️ Debes seleccionar un estado');
      return false;
    }
    return true;
  }

  verComandasMesa(mesa: Mesa): void {
    this.mesaSeleccionada = mesa;
    
    // Si la mesa está disponible, mostrar mensaje especial
    if (mesa.estado && (mesa.estado.nombre?.toLowerCase() === 'disponible')) {
      const confirmar = confirm(
        `La mesa ${mesa.idMesa} (${mesa.ubicacion}) está actualmente DISPONIBLE.\n\n` +
        `¿Deseas ver el historial de comandas de esta mesa?`
      );
      
      if (!confirmar) return;
    }
    
    this.cargandoFacturacion = true;
    this.facturacionMesa = null;

    this.comandaService.obtenerFacturacionMesa(mesa.idMesa).subscribe({
      next: (facturacion) => {
        this.facturacionMesa = facturacion;
        this.cargandoFacturacion = false;
        setTimeout(() => {
          const modalElement = document.getElementById('comandasMesaModal');
          if (modalElement) {
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
          }
        }, 100);
      },
      error: (err) => {
        console.error('Error al cargar facturación:', err);
        alert('Error al cargar las comandas de la mesa');
        this.cargandoFacturacion = false;
      }
    });
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return 'N/A';
    try {
      const date = new Date(fecha);
      return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return fecha;
    }
  }

  formatearMoneda(valor: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(valor);
  }

  imprimirFactura(): void {
    if (!this.facturacionMesa) return;
    
    // Crear ventana de impresión
    const ventanaImpresion = window.open('', '_blank');
    if (!ventanaImpresion) {
      alert('Por favor, permite las ventanas emergentes para imprimir la factura');
      return;
    }

    const contenido = `
      <!DOCTYPE html>
      <html>
      <head>
        <title>Factura - Mesa ${this.facturacionMesa.idMesa}</title>
        <style>
          body { font-family: Arial, sans-serif; padding: 20px; }
          .header { text-align: center; margin-bottom: 30px; }
          .info { margin-bottom: 20px; }
          table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
          th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
          th { background-color: #f2f2f2; }
          .total { font-size: 18px; font-weight: bold; }
          .footer { margin-top: 30px; text-align: center; color: #666; }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>RESTAURANTE</h1>
          <h2>FACTURA</h2>
        </div>
        <div class="info">
          <p><strong>Mesa:</strong> ${this.facturacionMesa.idMesa} - ${this.facturacionMesa.ubicacionMesa}</p>
          <p><strong>Fecha:</strong> ${new Date().toLocaleString('es-ES')}</p>
        </div>
        <table>
          <thead>
            <tr>
              <th>ID Comanda</th>
              <th>Fecha</th>
              <th>Estado</th>
              <th>Mesero</th>
              <th>Productos</th>
              <th style="text-align: right;">Total</th>
            </tr>
          </thead>
          <tbody>
            ${this.facturacionMesa.comandas.map(c => `
              <tr>
                <td>#${c.idComanda}</td>
                <td>${this.formatearFecha(c.fecha)}</td>
                <td>${c.estado}</td>
                <td>${c.mesero}</td>
                <td>${c.cantidadProductos}</td>
                <td style="text-align: right;">${this.formatearMoneda(c.total)}</td>
              </tr>
            `).join('')}
          </tbody>
          <tfoot>
            <tr>
              <td colspan="5" style="text-align: right;"><strong>TOTAL A PAGAR:</strong></td>
              <td style="text-align: right;" class="total">${this.formatearMoneda(this.facturacionMesa.totalAPagar)}</td>
            </tr>
          </tfoot>
        </table>
        <div class="footer">
          <p>Gracias por su visita</p>
        </div>
      </body>
      </html>
    `;

    ventanaImpresion.document.write(contenido);
    ventanaImpresion.document.close();
    ventanaImpresion.print();
  }

  finalizarYLiberarMesa(): void {
    if (!this.mesaSeleccionada || !this.facturacionMesa) return;

    // Verificar token antes de hacer la petición
    const token = this.authService.getToken();
    if (!token || token.trim() === '') {
      alert('⚠️ No hay sesión activa. Por favor, inicia sesión nuevamente.');
      return;
    }

    const confirmar = confirm(
      `¿Deseas finalizar todas las comandas pendientes de la mesa ${this.mesaSeleccionada.idMesa} y liberarla?\n\n` +
      `Esto marcará ${this.facturacionMesa.comandasPendientes} comanda(s) como completada(s) y cambiará el estado de la mesa a "Disponible".`
    );

    if (!confirmar) return;

    console.log('🔍 Finalizando y liberando mesa:', this.mesaSeleccionada.idMesa);
    console.log('🔍 Token disponible:', token ? 'Sí' : 'No');

    this.comandaService.finalizarYLiberarMesa(this.mesaSeleccionada.idMesa).subscribe({
      next: (resultado) => {
        console.log('✅ Resultado:', resultado);
        alert(`✅ ${resultado.message}\n\nComandas finalizadas: ${resultado.comandasFinalizadas}\nMesa liberada: ${resultado.mesaLiberada ? 'Sí' : 'No'}`);
        
        // Cerrar el modal
        const modalElement = document.getElementById('comandasMesaModal');
        if (modalElement) {
          const modal = bootstrap.Modal.getInstance(modalElement);
          if (modal) {
            modal.hide();
          }
        }
        
        // Recargar datos para actualizar el estado de la mesa
        this.cargarDatos();
        
        // Limpiar la facturación para que se recargue si se vuelve a abrir
        this.facturacionMesa = null;
      },
      error: (err) => {
        console.error('❌ Error al finalizar y liberar mesa:', err);
        console.error('❌ Status:', err.status);
        console.error('❌ Status Text:', err.statusText);
        console.error('❌ Error completo:', JSON.stringify(err, null, 2));
        
        if (err.status === 403 || err.status === 401) {
          alert('⚠️ Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
          // Opcional: redirigir al login
          // this.router.navigate(['/login']);
        } else {
          alert('Error al finalizar las comandas y liberar la mesa: ' + (err.error?.error || err.message || 'Error desconocido'));
        }
      }
    });
  }

  marcarComandaComoPagada(idComanda: number): void {
    if (!confirm(`¿Deseas marcar la comanda #${idComanda} como pagada?`)) {
      return;
    }

    this.comandaService.marcarComandaComoPagada(idComanda).subscribe({
      next: () => {
        console.log('✅ Comanda marcada como pagada:', idComanda);
        // Recargar la facturación
        if (this.mesaSeleccionada) {
          this.verComandasMesa(this.mesaSeleccionada);
        }
      },
      error: (err) => {
        console.error('Error al marcar comanda como pagada:', err);
        alert('Error al marcar la comanda como pagada: ' + (err.error?.error || err.message));
      }
    });
  }

  marcarTodasComandasPagadas(): void {
    if (!this.mesaSeleccionada || !this.facturacionMesa) return;

    const comandasAPagar = this.facturacionMesa.comandas.filter(c => 
      !c.pagada && (c.estado === 'Completado' || c.estado === 'Completada')
    ).length;

    if (comandasAPagar === 0) {
      alert('⚠️ No hay comandas pendientes de pago');
      return;
    }

    const confirmar = confirm(
      `¿Deseas marcar todas las comandas completadas como pagadas?\n\n` +
      `Se marcarán ${comandasAPagar} comanda(s) como pagada(s).`
    );

    if (!confirmar) return;

    this.comandaService.marcarTodasComandasPagadas(this.mesaSeleccionada.idMesa).subscribe({
      next: (resultado) => {
        console.log('✅ Resultado:', resultado);
        alert(`✅ ${resultado.message}\n\nComandas marcadas como pagadas: ${resultado.comandasPagadas}`);
        
        // Recargar la facturación
        if (this.mesaSeleccionada) {
          this.verComandasMesa(this.mesaSeleccionada);
        }
      },
      error: (err) => {
        console.error('Error al marcar comandas como pagadas:', err);
        alert('Error al marcar las comandas como pagadas: ' + (err.error?.error || err.message));
      }
    });
  }
}

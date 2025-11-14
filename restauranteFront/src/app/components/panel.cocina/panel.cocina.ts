import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComandaService, Comanda, UsuarioSimple } from '../../service/comanda.service';
import { UsuarioService, Usuario } from '../../service/usuario.service';
import { MesaService, Mesa } from '../../service/mesa.service';
import { ProductoService, Producto } from '../../service/producto.service';
import { AuthService } from '../../service/auth.service';
import { HttpClient } from '@angular/common/http';

declare var bootstrap: any;

@Component({
  selector: 'app-panel.cocina',
  imports: [CommonModule, FormsModule],
  templateUrl: './panel.cocina.html',
  styleUrl: './panel.cocina.css'
})
export class PanelCocina implements OnInit {
  private comandaService = inject(ComandaService);
  private usuarioService = inject(UsuarioService);
  private mesaService = inject(MesaService);
  private productoService = inject(ProductoService);
  private authService = inject(AuthService);
  private http = inject(HttpClient);

  comandas: Comanda[] = [];
  comandasPendientes: Comanda[] = [];
  comandasEnPreparacion: Comanda[] = [];
  comandasCompletadas: Comanda[] = [];
  comandasCanceladas: Comanda[] = [];
  
  cocineros: Usuario[] = [];
  meseros: Usuario[] = [];
  mesas: Mesa[] = [];
  productos: Producto[] = [];
  cargando = true;
  error: string | null = null;

  // Estadísticas
  estadisticas = {
    total: 0,
    pendientes: 0,
    enPreparacion: 0,
    completadas: 0,
    canceladas: 0
  };

  // Filtros
  filtroMesa: number | null = null;
  filtroCocinero: string | null = null;
  terminoBusqueda = '';

  // Modal
  comandaSeleccionada: Comanda | null = null;
  cocineroSeleccionado: string = '';

  // Nueva Comanda
  nuevaComanda = {
    idMesa: 0,
    idMesero: '',
    productosSeleccionados: [] as Array<{ producto: Producto; cantidad: number }>
  };
  productoSeleccionado: Producto | null = null;
  cantidadProducto: number = 1;

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = null;

    // Verificar autenticación
    const token = this.authService.getToken();
    if (!token || token.trim() === '') {
      this.error = 'Sesión expirada. Por favor, inicia sesión nuevamente.';
      this.cargando = false;
      setTimeout(() => {
        this.authService.logout();
        window.location.href = '/login';
      }, 2000);
      return;
    }

    // Cargar comandas y cocineros en paralelo
    this.comandaService.obtenerComandas().subscribe({
      next: (comandas) => {
        this.comandas = comandas;
        this.organizarComandasPorEstado();
        this.calcularEstadisticas();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar comandas:', err);
        if (err.status === 401 || err.status === 403) {
          this.error = 'Sesión expirada. Por favor, inicia sesión nuevamente.';
          setTimeout(() => {
            this.authService.logout();
            window.location.href = '/login';
          }, 2000);
        } else {
          this.error = 'Error al cargar las comandas';
        }
        this.cargando = false;
      }
    });

    // Cargar cocineros, meseros, mesas y productos
    this.usuarioService.obtenerUsuarios().subscribe({
      next: (usuarios) => {
        this.cocineros = usuarios.filter(u => 
          u.rol?.nombre?.toLowerCase() === 'cocinero'
        );
        this.meseros = usuarios.filter(u => 
          u.rol?.nombre?.toLowerCase() === 'mesero'
        );
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);
        this.cocineros = [];
        this.meseros = [];
      }
    });

    this.mesaService.obtenerMesas().subscribe({
      next: (mesas) => {
        this.mesas = mesas;
      },
      error: (err) => {
        console.error('Error al cargar mesas:', err);
        this.mesas = [];
      }
    });

    this.productoService.obtenerProductos().subscribe({
      next: (productos) => {
        this.productos = productos.filter(p => p.estado && p.stock > 0);
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.productos = [];
      }
    });
  }

  organizarComandasPorEstado(): void {
    this.comandasPendientes = [];
    this.comandasEnPreparacion = [];
    this.comandasCompletadas = [];
    this.comandasCanceladas = [];

    this.comandas.forEach(comanda => {
      const estadoNombre = comanda.estado?.nombre?.toLowerCase() || '';
      
      if (estadoNombre.includes('pendiente')) {
        this.comandasPendientes.push(comanda);
      } else if (estadoNombre.includes('preparacion') || estadoNombre.includes('preparación')) {
        this.comandasEnPreparacion.push(comanda);
      } else if (estadoNombre.includes('completada') || estadoNombre.includes('completado')) {
        this.comandasCompletadas.push(comanda);
      } else if (estadoNombre.includes('cancelada') || estadoNombre.includes('cancelado')) {
        this.comandasCanceladas.push(comanda);
      }
    });

    // Ordenar por fecha (más recientes primero)
    this.comandasPendientes.sort((a, b) => 
      new Date(b.fecha).getTime() - new Date(a.fecha).getTime()
    );
    this.comandasEnPreparacion.sort((a, b) => 
      new Date(b.fecha).getTime() - new Date(a.fecha).getTime()
    );
    this.comandasCompletadas.sort((a, b) => 
      new Date(b.fecha).getTime() - new Date(a.fecha).getTime()
    );
    this.comandasCanceladas.sort((a, b) => 
      new Date(b.fecha).getTime() - new Date(a.fecha).getTime()
    );
  }

  calcularEstadisticas(): void {
    this.estadisticas.total = this.comandas.length;
    this.estadisticas.pendientes = this.comandasPendientes.length;
    this.estadisticas.enPreparacion = this.comandasEnPreparacion.length;
    this.estadisticas.completadas = this.comandasCompletadas.length;
    this.estadisticas.canceladas = this.comandasCanceladas.length;
  }

  // Acciones de estado
  tomarComanda(comanda: Comanda): void {
    if (!this.cocineroSeleccionado) {
      alert('⚠️ Debes seleccionar un cocinero primero');
      this.abrirModalAsignarCocinero(comanda);
      return;
    }

    this.comandaService.marcarComoEnPreparacion(comanda.idComanda, this.cocineroSeleccionado).subscribe({
      next: () => {
        alert('✅ Comanda tomada en preparación');
        this.cargarDatos();
        this.cerrarModal('asignarCocineroModal');
      },
      error: (err) => {
        console.error('Error al tomar comanda:', err);
        alert('Error al tomar la comanda: ' + (err.error?.error || err.message));
      }
    });
  }

  completarComanda(comanda: Comanda): void {
    if (confirm(`¿Marcar la comanda #${comanda.idComanda} como completada?`)) {
      this.comandaService.marcarComoCompletada(comanda.idComanda).subscribe({
        next: () => {
          alert('✅ Comanda marcada como completada');
          this.cargarDatos();
        },
        error: (err) => {
          console.error('Error al completar comanda:', err);
          alert('Error al completar la comanda: ' + (err.error?.error || err.message));
        }
      });
    }
  }

  cancelarComanda(comanda: Comanda): void {
    if (confirm(`¿Cancelar la comanda #${comanda.idComanda}?`)) {
      this.comandaService.marcarComoCancelada(comanda.idComanda).subscribe({
        next: () => {
          alert('✅ Comanda cancelada');
          this.cargarDatos();
        },
        error: (err) => {
          console.error('Error al cancelar comanda:', err);
          alert('Error al cancelar la comanda: ' + (err.error?.error || err.message));
        }
      });
    }
  }

  // Modals
  abrirModalDetalles(comanda: Comanda): void {
    this.comandaSeleccionada = comanda;
    setTimeout(() => {
      const modalElement = document.getElementById('detallesComandaModal');
      if (modalElement) {
        let modal = bootstrap.Modal.getInstance(modalElement);
        if (!modal) {
          modal = new bootstrap.Modal(modalElement);
        }
        modal.show();
      }
    }, 0);
  }

  abrirModalAsignarCocinero(comanda: Comanda): void {
    this.comandaSeleccionada = comanda;
    this.cocineroSeleccionado = comanda.cocinero?.idUsuario || '';
    setTimeout(() => {
      const modalElement = document.getElementById('asignarCocineroModal');
      if (modalElement) {
        let modal = bootstrap.Modal.getInstance(modalElement);
        if (!modal) {
          modal = new bootstrap.Modal(modalElement);
        }
        modal.show();
      }
    }, 0);
  }

  cerrarModal(idModal: string): void {
    const modalElement = document.getElementById(idModal);
    if (modalElement) {
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }

  asignarCocinero(): void {
    if (!this.comandaSeleccionada || !this.cocineroSeleccionado) {
      alert('⚠️ Debes seleccionar un cocinero');
      return;
    }

    this.comandaService.asignarCocinero(
      this.comandaSeleccionada.idComanda,
      this.cocineroSeleccionado
    ).subscribe({
      next: () => {
        alert('✅ Cocinero asignado exitosamente');
        this.cargarDatos();
        this.cerrarModal('asignarCocineroModal');
      },
      error: (err) => {
        console.error('Error al asignar cocinero:', err);
        alert('Error al asignar cocinero: ' + (err.error?.error || err.message));
      }
    });
  }

  // Utilidades
  formatearMoneda(valor: number | null | undefined): string {
    if (valor == null || isNaN(valor)) {
      return '$0';
    }
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(valor);
  }

  formatearFecha(fecha: string): string {
    try {
      const date = new Date(fecha);
      return new Intl.DateTimeFormat('es-CO', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    } catch {
      return fecha;
    }
  }

  obtenerColorEstado(estado: string | null | undefined): string {
    if (!estado) return 'secondary';
    const estadoLower = estado.toLowerCase();
    if (estadoLower.includes('pendiente')) return 'warning';
    if (estadoLower.includes('preparacion') || estadoLower.includes('preparación')) return 'info';
    if (estadoLower.includes('completada') || estadoLower.includes('completado')) return 'success';
    if (estadoLower.includes('cancelada') || estadoLower.includes('cancelado')) return 'danger';
    return 'secondary';
  }

  obtenerIconoEstado(estado: string | null | undefined): string {
    if (!estado) return '❓';
    const estadoLower = estado.toLowerCase();
    if (estadoLower.includes('pendiente')) return '⏳';
    if (estadoLower.includes('preparacion') || estadoLower.includes('preparación')) return '👨‍🍳';
    if (estadoLower.includes('completada') || estadoLower.includes('completado')) return '✅';
    if (estadoLower.includes('cancelada') || estadoLower.includes('cancelado')) return '❌';
    return '❓';
  }

  calcularTiempoTranscurrido(fecha: string): string {
    try {
      const ahora = new Date();
      const fechaComanda = new Date(fecha);
      const diffMs = ahora.getTime() - fechaComanda.getTime();
      const diffMin = Math.floor(diffMs / 60000);
      
      if (diffMin < 1) return 'Hace menos de 1 min';
      if (diffMin < 60) return `Hace ${diffMin} min`;
      
      const diffHoras = Math.floor(diffMin / 60);
      return `Hace ${diffHoras}h ${diffMin % 60}min`;
    } catch {
      return 'N/A';
    }
  }

  refrescar(): void {
    this.cargarDatos();
  }

  // Crear Comanda
  abrirModalNuevaComanda(): void {
    console.log('🔵 Abriendo modal nueva comanda...');
    this.nuevaComanda = {
      idMesa: 0,
      idMesero: '',
      productosSeleccionados: []
    };
    this.productoSeleccionado = null;
    this.cantidadProducto = 1;
    
    // Verificar que Bootstrap esté disponible
    if (typeof (window as any).bootstrap === 'undefined') {
      console.error('❌ Bootstrap no está disponible');
      alert('Error: Bootstrap no está cargado. Por favor, recarga la página.');
      return;
    }
    
    const bootstrapLib = (window as any).bootstrap;
    
    // Usar setTimeout para asegurar que el DOM esté listo
    setTimeout(() => {
      // Intentar múltiples formas de encontrar el elemento
      let modalElement = document.getElementById('nuevaComandaModal');
      
      if (!modalElement) {
        // Intentar buscar por querySelector
        modalElement = document.querySelector('#nuevaComandaModal') as HTMLElement;
      }
      
      console.log('🔍 Buscando elemento modal:', modalElement);
      console.log('🔍 Todos los modales en el DOM:', document.querySelectorAll('.modal'));
      
      if (modalElement) {
        try {
          // Verificar si ya existe una instancia del modal
          let modal = bootstrapLib.Modal.getInstance(modalElement);
          if (!modal) {
            console.log('📦 Creando nueva instancia del modal');
            modal = new bootstrapLib.Modal(modalElement, {
              backdrop: true,
              keyboard: true,
              focus: true
            });
          } else {
            console.log('♻️ Reutilizando instancia existente del modal');
          }
          
          // Verificar clases del modal antes de mostrar
          console.log('🔍 Clases del modal antes de show:', modalElement.className);
          console.log('🔍 Estilos del modal:', window.getComputedStyle(modalElement));
          
          console.log('✅ Mostrando modal');
          modal.show();
          
          // Verificar después de show
          setTimeout(() => {
            console.log('🔍 Clases del modal después de show:', modalElement.className);
            console.log('🔍 Display del modal:', window.getComputedStyle(modalElement).display);
            console.log('🔍 Visibility del modal:', window.getComputedStyle(modalElement).visibility);
            console.log('🔍 Z-index del modal:', window.getComputedStyle(modalElement).zIndex);
            
            // Si el modal no se muestra, intentar forzar el display
            if (modalElement.classList.contains('show')) {
              console.log('✅ Modal tiene clase "show"');
            } else {
              console.warn('⚠️ Modal NO tiene clase "show", forzando...');
              modalElement.classList.add('show');
              modalElement.style.display = 'block';
              document.body.classList.add('modal-open');
              
              // Crear backdrop manualmente si no existe
              let backdrop = document.querySelector('.modal-backdrop');
              if (!backdrop) {
                backdrop = document.createElement('div');
                backdrop.className = 'modal-backdrop fade show';
                document.body.appendChild(backdrop);
              }
            }
          }, 100);
        } catch (error) {
          console.error('❌ Error al crear/mostrar modal:', error);
          console.error('❌ Stack trace:', (error as Error).stack);
          alert('Error al abrir el modal: ' + (error as Error).message);
        }
      } else {
        console.error('❌ No se encontró el elemento del modal nuevaComandaModal');
        console.error('🔍 Todos los elementos con ID:', Array.from(document.querySelectorAll('[id]')).map(el => el.id));
        alert('Error: No se pudo encontrar el modal. Por favor, verifica la consola para más detalles.');
      }
    }, 200);
  }

  seleccionarProducto(idProducto: number | string | null): void {
    console.log('🔍 seleccionarProducto llamado con:', idProducto, 'tipo:', typeof idProducto);
    
    if (idProducto === null || idProducto === 'null' || idProducto === '') {
      this.productoSeleccionado = null;
      console.log('⚠️ Producto deseleccionado');
      return;
    }
    
    // Convertir a número si viene como string
    const idProductoNum = typeof idProducto === 'string' ? parseInt(idProducto, 10) : idProducto;
    console.log('🔍 ID producto convertido a número:', idProductoNum);
    console.log('🔍 Productos disponibles:', this.productos.length);
    console.log('🔍 IDs de productos:', this.productos.map(p => ({ id: p.idProducto, nombre: p.nombre })));
    
    this.productoSeleccionado = this.productos.find(p => p.idProducto === idProductoNum) || null;
    
    if (this.productoSeleccionado) {
      console.log('✅ Producto seleccionado:', this.productoSeleccionado.nombre, 'ID:', this.productoSeleccionado.idProducto);
    } else {
      console.error('❌ Producto NO encontrado con ID:', idProductoNum);
      console.error('❌ Productos disponibles:', this.productos);
    }
  }

  agregarProductoALista(): void {
    console.log('🔄 agregarProductoALista llamado');
    console.log('🔄 productoSeleccionado:', this.productoSeleccionado);
    console.log('🔄 cantidadProducto:', this.cantidadProducto);
    console.log('🔄 tipo de productoSeleccionado:', typeof this.productoSeleccionado);
    
    if (!this.productoSeleccionado) {
      console.error('❌ No hay producto seleccionado');
      alert('⚠️ Debes seleccionar un producto y una cantidad válida');
      return;
    }
    
    if (!this.cantidadProducto || this.cantidadProducto < 1) {
      console.error('❌ Cantidad inválida:', this.cantidadProducto);
      alert('⚠️ Debes seleccionar un producto y una cantidad válida');
      return;
    }

    if (this.cantidadProducto > this.productoSeleccionado.stock) {
      alert(`⚠️ No hay suficiente stock. Stock disponible: ${this.productoSeleccionado.stock}`);
      return;
    }

    // Verificar si el producto ya está en la lista
    const index = this.nuevaComanda.productosSeleccionados.findIndex(
      p => p.producto.idProducto === this.productoSeleccionado!.idProducto
    );

    if (index >= 0) {
      // Actualizar cantidad
      const nuevaCantidad = this.nuevaComanda.productosSeleccionados[index].cantidad + this.cantidadProducto;
      if (nuevaCantidad > this.productoSeleccionado.stock) {
        alert(`⚠️ No hay suficiente stock. Stock disponible: ${this.productoSeleccionado.stock}`);
        return;
      }
      this.nuevaComanda.productosSeleccionados[index].cantidad = nuevaCantidad;
    } else {
      // Agregar nuevo producto
      this.nuevaComanda.productosSeleccionados.push({
        producto: this.productoSeleccionado,
        cantidad: this.cantidadProducto
      });
    }

    // Resetear selección
    this.productoSeleccionado = null;
    this.cantidadProducto = 1;
  }

  eliminarProductoDeLista(index: number): void {
    this.nuevaComanda.productosSeleccionados.splice(index, 1);
  }

  calcularTotalComanda(): number {
    return this.nuevaComanda.productosSeleccionados.reduce((total, item) => {
      return total + (item.producto.precio * item.cantidad);
    }, 0);
  }

  crearComanda(): void {
    if (!this.nuevaComanda.idMesa || this.nuevaComanda.idMesa === 0) {
      alert('⚠️ Debes seleccionar una mesa');
      return;
    }

    if (!this.nuevaComanda.idMesero || this.nuevaComanda.idMesero.trim() === '') {
      alert('⚠️ Debes seleccionar un mesero');
      return;
    }

    if (this.nuevaComanda.productosSeleccionados.length === 0) {
      alert('⚠️ Debes agregar al menos un producto a la comanda');
      return;
    }

    // Crear comanda básica primero
    // Asegurar que idMesa sea un número
    const comandaData = {
      idMesa: Number(this.nuevaComanda.idMesa),
      idMesero: String(this.nuevaComanda.idMesero).trim()
    };

    console.log('📦 ========== CREANDO COMANDA ==========');
    console.log('📦 Datos a enviar:', JSON.stringify(comandaData, null, 2));
    console.log('📦 idMesa tipo:', typeof comandaData.idMesa, 'valor:', comandaData.idMesa);
    console.log('📦 idMesero tipo:', typeof comandaData.idMesero, 'valor:', comandaData.idMesero);

    this.http.post<any>(`http://localhost:8080/api/comandas/crear`, comandaData).subscribe({
      next: (comandaCreada) => {
        console.log('✅ ========== COMANDA CREADA EXITOSAMENTE ==========');
        console.log('✅ Comanda creada:', comandaCreada);
        console.log('✅ ID de comanda:', comandaCreada.idComanda);
        console.log('✅ Productos a agregar:', this.nuevaComanda.productosSeleccionados.length);
        
        // Agregar productos a la comanda
        const promesas = this.nuevaComanda.productosSeleccionados.map((item, index) => {
          const detalleData = {
            idComanda: comandaCreada.idComanda,
            idProducto: Number(item.producto.idProducto),
            cantidad: Number(item.cantidad),
            precioUnitario: Number(item.producto.precio)
          };
          console.log(`📦 Agregando producto ${index + 1}:`, detalleData);
          return this.http.post(`http://localhost:8080/api/detalle-comandas/crear`, detalleData).toPromise();
        });

        Promise.all(promesas).then(() => {
          console.log('✅ Todos los productos agregados exitosamente');
          alert('✅ Comanda creada exitosamente con todos los productos');
          // Limpiar formulario
          this.nuevaComanda = {
            idMesa: 0,
            idMesero: '',
            productosSeleccionados: []
          };
          this.productoSeleccionado = null;
          this.cantidadProducto = 1;
          this.cerrarModal('nuevaComandaModal');
          this.cargarDatos();
        }).catch((err) => {
          console.error('❌ Error al agregar productos:', err);
          console.error('❌ Error completo:', JSON.stringify(err, null, 2));
          alert('⚠️ Comanda creada pero hubo errores al agregar algunos productos. Por favor, verifica.');
          this.cargarDatos();
        });
      },
      error: (err) => {
        console.error('❌ ========== ERROR AL CREAR COMANDA ==========');
        console.error('❌ Error completo:', err);
        console.error('❌ Status:', err.status);
        console.error('❌ Status Text:', err.statusText);
        console.error('❌ Error body:', err.error);
        console.error('❌ Error message:', err.message);
        
        let mensajeError = 'Error al crear la comanda';
        if (err.error?.error) {
          mensajeError = err.error.error;
        } else if (err.message) {
          mensajeError = err.message;
        }
        
        alert('❌ Error al crear la comanda: ' + mensajeError);
      }
    });
  }
}

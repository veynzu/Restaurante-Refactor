import { Component, OnInit, inject } from '@angular/core';
import { DashboardService, DashboardEstadisticas } from '../../service/dashboard.service';
import { ProductoService, Producto, ProductoCreateRequest } from '../../service/producto.service';
import { CategoriaService, Categoria } from '../../service/categoria.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';

declare var bootstrap: any;

@Component({
  selector: 'app-panel-control',
  imports: [CommonModule, FormsModule],
  templateUrl: './panel-control.html',
  styleUrl: './panel-control.css'
})
export class PanelControl implements OnInit {
  private dashboardService = inject(DashboardService);
  private productoService = inject(ProductoService);
  private categoriaService = inject(CategoriaService);
  private authService = inject(AuthService);

  estadisticas: DashboardEstadisticas | null = null;
  cargando = true;
  error: string | null = null;

  // Productos
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  cargandoProductos = false;
  terminoBusquedaProductos = '';

  // Modal
  nuevoProducto: ProductoCreateRequest = {
    nombre: '',
    precio: 0,
    stock: 0,
    idCategoria: 0
  };
  productoSeleccionado: Producto | null = null;
  productoEliminar: Producto | null = null;

  ngOnInit(): void {
    console.log('🚀 ========== PANEL CONTROL INICIALIZADO ==========');
    console.log('🚀 ngOnInit ejecutado');
    console.warn('⚠️ PANEL CONTROL INICIALIZADO - VERIFICA LA CONSOLA');
    console.error('❌ ESTE ES UN LOG DE ERROR PARA VERIFICAR QUE LA CONSOLA FUNCIONA');
    this.cargarEstadisticas();
    // Cargar categorías primero, luego productos (cargarProductos se llama desde cargarCategorias)
    console.log('🚀 Llamando a cargarCategorias()...');
    this.cargarCategorias();
  }

  cargarEstadisticas(): void {
    this.cargando = true;
    this.error = null;
    
    this.dashboardService.obtenerEstadisticas().subscribe({
      next: (data) => {
        // Asegurar que todos los campos estén inicializados
        this.estadisticas = {
          ...data,
          ventasSemana: data.ventasSemana != null ? Number(data.ventasSemana) : 0,
          ventasHoy: data.ventasHoy != null ? Number(data.ventasHoy) : 0,
          comandasPorEstado: data.comandasPorEstado || {},
          comandasRecientes: data.comandasRecientes || [],
          productosMasVendidos: data.productosMasVendidos || []
        };
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar estadísticas:', err);
        if (err.status === 403) {
          this.error = 'Error de autenticación. Por favor, inicia sesión nuevamente.';
        } else if (err.status === 401) {
          this.error = 'Sesión expirada. Por favor, inicia sesión nuevamente.';
        } else {
          this.error = 'Error al cargar las estadísticas: ' + (err.error?.error || err.message || 'Error desconocido');
        }
        this.cargando = false;
      }
    });
  }

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

  obtenerColorEstado(estado: string | null): string {
    if (!estado) return 'secondary';
    switch (estado.toLowerCase()) {
      case 'pendiente': return 'warning';
      case 'en preparacion': return 'info';
      case 'completada':
      case 'completado': return 'success';
      case 'cancelada':
      case 'cancelado': return 'danger';
      default: return 'secondary';
    }
  }

  // Métodos para Productos
  cargarProductos(): void {
    console.log('🔄 ========== CARGAR PRODUCTOS LLAMADO ==========');
    console.log('🔄 cargarProductos() ejecutado');
    this.cargandoProductos = true;
    console.log('🔄 Llamando a productoService.obtenerProductos()...');
    this.productoService.obtenerProductos().subscribe({
      next: (productos) => {
        console.log('✅ ========== PRODUCTOS RECIBIDOS DEL BACKEND ==========');
        console.warn('⚠️ PRODUCTOS RECIBIDOS - VERIFICA LA CONSOLA');
        console.error('❌ PRODUCTOS RECIBIDOS - LOG DE ERROR PARA VERIFICAR');
        console.log('📦 Productos recibidos del backend:', productos);
        console.log('📦 Tipo de productos:', Array.isArray(productos) ? 'Array' : typeof productos);
        console.log('📦 Cantidad de productos:', productos?.length || 0);
        console.log('📦 Categorías disponibles:', this.categorias);
        console.log('📦 Cantidad de categorías:', this.categorias?.length || 0);
        
        // Log detallado del primer producto
        if (productos && productos.length > 0) {
          const primerProducto = productos[0];
          console.log('🔍 ========== DETALLE DEL PRIMER PRODUCTO ==========');
          console.log('🔍 Producto completo (JSON):', JSON.stringify(primerProducto, null, 2));
          console.log('🔍 Tiene propiedad idCategoria?:', 'idCategoria' in primerProducto);
          console.log('🔍 Valor de idCategoria:', primerProducto.idCategoria);
          console.log('🔍 Tipo de idCategoria:', typeof primerProducto.idCategoria);
          console.log('🔍 Valor de categoria:', primerProducto.categoria);
          console.log('🔍 Tipo de categoria:', typeof primerProducto.categoria);
          console.log('🔍 Todas las propiedades:', Object.keys(primerProducto));
        }
        
        // Normalizar la categoría: siempre asegurar que tenga un valor válido
        this.productos = productos.map(p => {
          console.log('🔍 Procesando producto ID:', p.idProducto);
          console.log('🔍 - categoria recibida:', p.categoria, 'tipo:', typeof p.categoria);
          console.log('🔍 - idCategoria recibido:', p.idCategoria, 'tipo:', typeof p.idCategoria);
          console.log('🔍 - Categorías disponibles en memoria:', this.categorias.length);
          
          // Obtener el nombre de la categoría del backend (puede venir como string)
          const nombreCategoriaBackend = typeof p.categoria === 'string' ? p.categoria : null;
          
          // PRIORIDAD 1: Si tenemos idCategoria, buscar la categoría completa en la lista
          if (p.idCategoria != null && p.idCategoria > 0) {
            const categoriaEncontrada = this.categorias.find(c => c.idCategoria === p.idCategoria);
            if (categoriaEncontrada) {
              console.log('✅ Categoría encontrada por ID:', categoriaEncontrada.nombre);
              p.categoria = categoriaEncontrada;
            } else {
              // No se encontró en la lista, pero tenemos el nombre del backend
              const nombreFinal = nombreCategoriaBackend && nombreCategoriaBackend.trim() !== '' 
                ? nombreCategoriaBackend 
                : 'Sin categoría';
              console.log('⚠️ Categoría no encontrada por ID, usando nombre del backend:', nombreFinal);
              p.categoria = { idCategoria: p.idCategoria, nombre: nombreFinal };
            }
          } 
          // PRIORIDAD 2: Si no hay idCategoria pero tenemos nombre como string, buscar por nombre
          else if (nombreCategoriaBackend && nombreCategoriaBackend.trim() !== '') {
            const categoriaEncontrada = this.categorias.find(c => c.nombre === nombreCategoriaBackend);
            if (categoriaEncontrada) {
              console.log('✅ Categoría encontrada por nombre:', categoriaEncontrada.nombre);
              p.categoria = categoriaEncontrada;
            } else {
              // No se encontró, pero tenemos el nombre del backend
              console.log('⚠️ Categoría no encontrada por nombre, usando nombre del backend:', nombreCategoriaBackend);
              p.categoria = { idCategoria: 0, nombre: nombreCategoriaBackend };
            }
          }
          // PRIORIDAD 3: Si la categoría ya es un objeto, verificar que tenga nombre
          else if (p.categoria && typeof p.categoria === 'object' && 'nombre' in p.categoria) {
            console.log('✅ Categoría ya es objeto válido:', p.categoria.nombre);
            // Ya está bien, no hacer nada
          }
          // PRIORIDAD 4: Si no hay nada, crear un objeto con "Sin categoría"
          else {
            console.log('❌ No hay categoría válida, usando "Sin categoría"');
            p.categoria = { idCategoria: 0, nombre: 'Sin categoría' };
          }
          
          console.log('✅ Producto normalizado ID:', p.idProducto, 'Categoría final:', 
            typeof p.categoria === 'object' ? p.categoria.nombre : p.categoria);
          return p;
        });
        console.log('✅ Productos cargados y normalizados:', this.productos);
        this.cargandoProductos = false;
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.cargandoProductos = false;
      }
    });
  }

  cargarCategorias(): void {
    console.log('🔄 ========== CARGAR CATEGORIAS LLAMADO ==========');
    console.log('🔄 cargarCategorias() ejecutado');
    console.log('🔄 Llamando a categoriaService.obtenerCategorias()...');
    this.categoriaService.obtenerCategorias().subscribe({
      next: (categorias) => {
        console.log('✅ ========== CATEGORIAS RECIBIDAS DEL BACKEND ==========');
        console.log('📋 Categorías cargadas:', categorias);
        console.log('📋 Tipo de categorías:', Array.isArray(categorias) ? 'Array' : typeof categorias);
        console.log('📋 Cantidad de categorías:', categorias?.length || 0);
        this.categorias = categorias;
        // Después de cargar categorías, cargar productos para poder normalizar
        // Siempre cargar productos después de categorías para asegurar que estén disponibles
        console.log('🔄 Ahora llamando a cargarProductos()...');
        this.cargarProductos();
      },
      error: (err) => {
        console.error('❌ ========== ERROR AL CARGAR CATEGORIAS ==========');
        console.error('Error al cargar categorías:', err);
        console.error('Error completo:', JSON.stringify(err, null, 2));
        // Aún así intentar cargar productos, pero sin categorías
        console.log('🔄 Intentando cargar productos sin categorías...');
        this.cargarProductos();
      }
    });
  }

  buscarProductos(): Producto[] {
    if (!this.terminoBusquedaProductos.trim()) {
      return this.productos;
    }
    const termino = this.terminoBusquedaProductos.toLowerCase().trim();
    return this.productos.filter(p => {
      const nombreMatch = p.nombre.toLowerCase().includes(termino);
      // Verificar si categoria es un objeto y tiene nombre
      const categoriaMatch = p.categoria && typeof p.categoria === 'object' 
        ? p.categoria.nombre?.toLowerCase().includes(termino) 
        : typeof p.categoria === 'string' 
          ? p.categoria.toLowerCase().includes(termino)
          : false;
      return nombreMatch || categoriaMatch;
    });
  }

  abrirModalNuevoProducto(): void {
    this.nuevoProducto = {
      nombre: '',
      precio: 0,
      stock: 0,
      idCategoria: 0
    };
    setTimeout(() => {
      const modalElement = document.getElementById('nuevoProductoModal');
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }
    }, 0);
  }

  crearProducto(): void {
    if (!this.validarFormularioProducto(this.nuevoProducto)) {
      return;
    }

    // Asegurar que idCategoria sea un número
    const productoParaEnviar = {
      ...this.nuevoProducto,
      idCategoria: Number(this.nuevoProducto.idCategoria)
    };

    console.log('📦 Creando producto con datos:', JSON.stringify(productoParaEnviar, null, 2));
    console.log('📦 idCategoria:', productoParaEnviar.idCategoria);
    console.log('📦 Tipo de idCategoria:', typeof productoParaEnviar.idCategoria);
    console.log('📦 Categorías disponibles:', this.categorias);

    this.productoService.crearProducto(productoParaEnviar).subscribe({
      next: (productoCreado) => {
        console.log('✅ Producto creado:', productoCreado);
        console.log('✅ Categoría en producto creado:', productoCreado.categoria);
        console.log('✅ idCategoria en producto creado:', productoCreado.idCategoria);
        alert('✅ Producto creado exitosamente');
        this.cerrarModal('nuevoProductoModal');
        // Normalizar la categoría del producto creado usando idCategoria si está disponible
        if (typeof productoCreado.categoria === 'string' && productoCreado.idCategoria) {
          const categoriaEncontrada = this.categorias.find(c => c.idCategoria === productoCreado.idCategoria);
          if (categoriaEncontrada) {
            productoCreado.categoria = categoriaEncontrada;
            console.log('✅ Categoría normalizada por ID:', categoriaEncontrada);
          } else {
            // Si no se encuentra por ID, buscar por nombre
            const categoriaPorNombre = this.categorias.find(c => c.nombre === productoCreado.categoria);
            if (categoriaPorNombre) {
              productoCreado.categoria = categoriaPorNombre;
              console.log('✅ Categoría normalizada por nombre:', categoriaPorNombre);
            }
          }
        }
        this.cargarProductos();
      },
      error: (err) => {
        console.error('❌ Error al crear producto:', err);
        console.error('❌ Error completo:', JSON.stringify(err, null, 2));
        alert('Error al crear el producto: ' + (err.error?.error || err.message));
      }
    });
  }

  abrirModalEliminar(producto: Producto): void {
    this.productoEliminar = producto;
    setTimeout(() => {
      const modalElement = document.getElementById('eliminarProductoModal');
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }
    }, 0);
  }

  eliminarProducto(): void {
    if (!this.productoEliminar) return;

    this.productoService.eliminarProducto(this.productoEliminar.idProducto).subscribe({
      next: () => {
        alert('✅ Producto eliminado exitosamente');
        this.cerrarModal('eliminarProductoModal');
        this.cargarProductos();
      },
      error: (err) => {
        console.error('Error al eliminar producto:', err);
        alert('Error al eliminar el producto: ' + (err.error?.error || err.message));
      }
    });
  }

  toggleEstadoProducto(producto: Producto): void {
    const accion = producto.estado ? 'desactivar' : 'activar';
    if (!confirm(`¿Estás seguro de que deseas ${accion} el producto "${producto.nombre}"?`)) {
      return;
    }

    if (producto.estado) {
      this.productoService.desactivarProducto(producto.idProducto).subscribe({
        next: (productoActualizado) => {
          console.log('✅ Producto desactivado:', productoActualizado);
          alert('✅ Producto desactivado exitosamente');
          // Actualizar el producto en la lista sin recargar todo
          const index = this.productos.findIndex(p => p.idProducto === producto.idProducto);
          if (index >= 0) {
            this.productos[index].estado = productoActualizado.estado || false;
          }
          // También recargar para asegurar que todo esté sincronizado
          this.cargarProductos();
        },
        error: (err) => {
          console.error('❌ Error al desactivar producto:', err);
          console.error('❌ Error completo:', JSON.stringify(err, null, 2));
          alert('Error al desactivar el producto: ' + (err.error?.error || err.message));
        }
      });
    } else {
      this.productoService.activarProducto(producto.idProducto).subscribe({
        next: (productoActualizado) => {
          console.log('✅ Producto activado:', productoActualizado);
          alert('✅ Producto activado exitosamente');
          // Actualizar el producto en la lista sin recargar todo
          const index = this.productos.findIndex(p => p.idProducto === producto.idProducto);
          if (index >= 0) {
            this.productos[index].estado = productoActualizado.estado || true;
          }
          // También recargar para asegurar que todo esté sincronizado
          this.cargarProductos();
        },
        error: (err) => {
          console.error('❌ Error al activar producto:', err);
          console.error('❌ Error completo:', JSON.stringify(err, null, 2));
          alert('Error al activar el producto: ' + (err.error?.error || err.message));
        }
      });
    }
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

  validarFormularioProducto(producto: ProductoCreateRequest): boolean {
    if (!producto.nombre || producto.nombre.trim().length === 0) {
      alert('⚠️ El nombre es obligatorio');
      return false;
    }
    if (!producto.precio || producto.precio <= 0) {
      alert('⚠️ El precio debe ser mayor a 0');
      return false;
    }
    if (producto.stock < 0) {
      alert('⚠️ El stock no puede ser negativo');
      return false;
    }
    if (!producto.idCategoria || producto.idCategoria === 0) {
      alert('⚠️ Debes seleccionar una categoría');
      return false;
    }
    return true;
  }

  obtenerNombreCategoria(categoria: Categoria | string | null | undefined): string {
    // No hacer log aquí porque se llama muchas veces en el template
    if (!categoria) {
      return 'Sin categoría';
    }
    if (typeof categoria === 'string') {
      // Si es string, intentar encontrar la categoría en la lista
      if (categoria.trim() !== '') {
        return categoria;
      }
      return 'Sin categoría';
    }
    // Si es objeto, retornar el nombre
    return categoria.nombre || 'Sin categoría';
  }
}

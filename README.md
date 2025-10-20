# 🍽️ Sistema de Gestión de Restaurante

## 📋 Descripción
Sistema completo de gestión para restaurantes desarrollado con **Spring Boot 3.5.4** y **Angular 20**, siguiendo principios de Clean Code y arquitectura escalable.

## 🚀 Tecnologías

### Backend
- **Spring Boot 3.5.4** - Framework principal
- **Java 17** - Lenguaje de programación
- **MySQL 8.0** - Base de datos
- **JPA/Hibernate** - ORM
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de autenticación
- **Swagger/OpenAPI 3** - Documentación de API
- **Maven** - Gestión de dependencias
- **JUnit 5** - Testing

### Frontend (Próximamente)
- **Angular 20** - Framework frontend
- **TypeScript** - Lenguaje tipado
- **Angular Resources** - Para operaciones CRUD
- **Fetch API** - Comunicación HTTP
- **Angular Signals** - Estado reactivo

## 🏗️ Arquitectura

### Principios Aplicados
- ✅ **Clean Code** - Código limpio y mantenible
- ✅ **SOLID** - Principios de diseño orientado a objetos
- ✅ **Arquitectura en Capas** - Separación de responsabilidades
- ✅ **RESTful API** - Diseño de API REST
- ✅ **DTOs** - Transferencia de datos segura
- ✅ **JWT Authentication** - Autenticación stateless

### Estructura del Proyecto
```
src/main/java/com/example/demo/
├── config/          # Configuraciones (Swagger, Security)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
│   ├── request/    # DTOs de entrada
│   └── response/   # DTOs de salida
├── entity/         # Entidades JPA
├── mapper/         # Mappers Entity ↔ DTO
├── repository/     # Repositorios JPA
├── security/       # Configuración de seguridad
└── service/        # Lógica de negocio
```

## 📊 Base de Datos

### Entidades Principales
- **Usuario** - Gestión de usuarios y roles
- **Mesa** - Control de mesas del restaurante
- **Producto** - Catálogo de productos/menú
- **Comanda** - Órdenes de los clientes
- **DetalleComanda** - Detalles de cada orden
- **Categoria** - Categorías de productos
- **Estado** - Estados del sistema
- **Rol** - Roles de usuario
- **Telefono** - Teléfonos de usuarios

## 🔐 Autenticación

### Usuarios por Defecto
- **Administrador:** `admin@restaurante.com` / `admin123`
- **Mesero:** `mesero@restaurante.com` / `mesero123`
- **Cocinero:** `cocinero@restaurante.com` / `cocinero123`

### Endpoints de Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario
- `GET /api/auth/verify` - Verificar token

## 📚 API Endpoints

### Total: 120+ Endpoints REST

#### Gestión de Usuarios
- `GET /api/usuarios` - Listar usuarios
- `POST /api/usuarios` - Crear usuario
- `GET /api/usuarios/{id}` - Obtener usuario
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

#### Gestión de Mesas
- `GET /api/mesas` - Listar mesas
- `POST /api/mesas` - Crear mesa
- `PUT /api/mesas/{id}/ocupar` - Ocupar mesa
- `PUT /api/mesas/{id}/liberar` - Liberar mesa

#### Gestión de Productos
- `GET /api/productos` - Listar productos
- `POST /api/productos` - Crear producto
- `GET /api/productos/{id}` - Obtener producto
- `PUT /api/productos/{id}` - Actualizar producto

#### Gestión de Comandas
- `GET /api/comandas` - Listar comandas
- `POST /api/comandas` - Crear comanda
- `GET /api/comandas/{id}` - Obtener comanda
- `PUT /api/comandas/{id}/estado` - Cambiar estado

#### Otros Endpoints
- `GET /api/estados` - Estados del sistema
- `GET /api/roles` - Roles de usuario
- `GET /api/categorias` - Categorías de productos
- `GET /api/telefonos` - Teléfonos
- `GET /api/detalle-comandas` - Detalles de comandas

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- XAMPP (opcional, para phpMyAdmin)

### Configuración de Base de Datos
1. **Crear base de datos:**
   ```sql
   CREATE DATABASE restaurante_db;
   ```

2. **Configurar `application.properties`:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/restaurante_db
   spring.datasource.username=root
   spring.datasource.password=tu_password
   ```

### Ejecutar la Aplicación
```bash
# Clonar repositorio
git clone https://github.com/TU_USUARIO/Restaurante-Refactor.git
cd Restaurante-Refactor

# Compilar y ejecutar
./mvnw spring-boot:run

# O con Maven
mvn spring-boot:run
```

### Crear Datos Iniciales
```bash
# Ejecutar endpoint para crear datos iniciales
POST http://localhost:8080/api/test/datos-iniciales
```

## 📖 Documentación API

### Swagger UI
- **URL:** `http://localhost:8080/swagger-ui/index.html`
- **Documentación completa** de todos los endpoints
- **Interfaz interactiva** para probar la API

### Autenticación en Swagger
1. **Hacer login** en `/api/auth/login`
2. **Copiar el token JWT** de la respuesta
3. **Hacer clic en "Authorize" (🔒)** en Swagger UI
4. **Pegar el token** y hacer clic en "Authorize"

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=UsuarioServiceTest
```

### Cobertura de Tests
- ✅ **Repository Tests** - Pruebas de acceso a datos
- ✅ **Service Tests** - Pruebas de lógica de negocio
- ✅ **Controller Tests** - Pruebas de endpoints REST
- ✅ **Integration Tests** - Pruebas de integración

## 🔧 Configuración

### Variables de Entorno
```properties
# JWT Configuration
jwt.secret=miClaveSecretaSuperSeguraParaElRestaurante2025MinimoDe256BitsParaHS256
jwt.expiration=86400000

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/restaurante_db
spring.datasource.username=root
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## 📈 Características

### ✅ Implementado
- [x] **Backend completo** con Spring Boot
- [x] **Base de datos** con MySQL y JPA
- [x] **Autenticación JWT** con Spring Security
- [x] **DTOs y Mappers** para transferencia de datos
- [x] **Documentación Swagger** completa
- [x] **Tests JUnit** para todas las capas
- [x] **Clean Code** y principios SOLID
- [x] **Arquitectura escalable** en capas

### 🚧 En Desarrollo
- [ ] **Frontend Angular 20** (próximamente)
- [ ] **Interfaz de usuario** moderna y responsive
- [ ] **Gestión de inventario** en tiempo real
- [ ] **Reportes y estadísticas**

## 🤝 Contribución

1. **Fork** el proyecto
2. **Crear rama** para nueva funcionalidad (`git checkout -b feature/nueva-funcionalidad`)
3. **Commit** cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. **Push** a la rama (`git push origin feature/nueva-funcionalidad`)
5. **Abrir Pull Request**

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

**Tu Nombre** - [@tu-usuario-github](https://github.com/tu-usuario-github)

## 📞 Contacto

- **Email:** tu-email@ejemplo.com
- **GitHub:** [@tu-usuario-github](https://github.com/tu-usuario-github)
- **LinkedIn:** [Tu Perfil](https://linkedin.com/in/tu-perfil)

---

⭐ **¡Dale una estrella al proyecto si te gusta!** ⭐

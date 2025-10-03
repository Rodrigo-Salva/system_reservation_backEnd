# System Reservation Back API

API backend para la gestión de reservas del sistema. Implementada con **Spring Boot**, con endpoints protegidos por **Spring Security** y documentación interactiva en **Swagger UI**.

---

## 📋 Tabla de Contenidos

- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Ejecución](#ejecución)
- [Endpoints](#endpoints)
- [Autenticación](#autenticación)
- [Ejemplos de Requests](#ejemplos-de-requests)
- [Códigos de Estado HTTP](#códigos-de-estado-http)
- [Buenas Prácticas](#buenas-prácticas)
- [Configuración](#configuración)
- [Notas](#notas)

---

## 🔧 Requisitos

- **Java**: 17 o superior
- **Maven**: 3.6+ o **Gradle**: 7.0+
- **Base de datos**: PostgreSQL/MySQL (configurable)
- **IDE recomendado**: IntelliJ IDEA o Eclipse

---

## 📦 Instalación

1. **Clonar el repositorio**:
```bash
git clone https://github.com/tu-usuario/system-reservation-back.git
cd system-reservation-back
```

2. **Configurar base de datos** en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reservation_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

3. **Instalar dependencias**:
```bash
./mvnw clean install
```

---

## 🚀 Ejecución

```bash
./mvnw spring-boot:run
```

- **Aplicación**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 📡 Endpoints

### Usuarios

| Método | Endpoint | Descripción | Auth Requerido |
|--------|----------|-------------|----------------|
| POST | `/api/users` | Crear un nuevo usuario | No |
| GET | `/api/users` | Obtener lista de usuarios | Sí |
| GET | `/api/users/{id}` | Obtener un usuario por ID | Sí |
| PUT | `/api/users/{id}` | Actualizar información de usuario | Sí |
| DELETE | `/api/users/{id}` | Eliminar usuario | Sí |

### Reservas

| Método | Endpoint | Descripción | Auth Requerido |
|--------|----------|-------------|----------------|
| POST | `/api/reservations` | Crear una nueva reserva | Sí |
| GET | `/api/reservations` | Listar todas las reservas | Sí |
| GET | `/api/reservations/{id}` | Obtener detalles de reserva | Sí |
| PUT | `/api/reservations/{id}` | Actualizar reserva | Sí |
| DELETE | `/api/reservations/{id}` | Cancelar reserva | Sí |

---

## 🔐 Autenticación

La API utiliza **Spring Security** con autenticación básica (HTTP Basic Auth).

### Desarrollo
Usuario de prueba generado automáticamente:
```
Username: user
Password: <check console output>
```

### Producción
Configurar `UserDetailsService` personalizado conectado a base de datos:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Implementación con repositorio de usuarios
}
```

### Headers requeridos
```http
Authorization: Basic <base64(username:password)>
Content-Type: application/json
```

---

## 📝 Ejemplos de Requests

### Crear Usuario

**Request:**
```http
POST /api/users
Content-Type: application/json

{
  "name": "Jefferson",
  "email": "jefferson@example.com",
  "password": "SecurePass123",
  "phoneNumber": "987654321"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Jefferson",
  "email": "jefferson@example.com",
  "phoneNumber": "987654321",
  "createdAt": "2025-10-03T10:30:00"
}
```

### Obtener Usuario

**Request:**
```http
GET /api/users/1
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Jefferson",
  "email": "jefferson@example.com",
  "phoneNumber": "987654321",
  "createdAt": "2025-10-03T10:30:00"
}
```

### Crear Reserva

**Request:**
```http
POST /api/reservations
Authorization: Basic dXNlcjpwYXNzd29yZA==
Content-Type: application/json

{
  "userId": 1,
  "serviceId": 5,
  "date": "2025-10-15",
  "time": "14:00",
  "notes": "Confirmar disponibilidad"
}
```

**Response (201 Created):**
```json
{
  "id": 10,
  "userId": 1,
  "serviceId": 5,
  "date": "2025-10-15",
  "time": "14:00",
  "status": "PENDING",
  "notes": "Confirmar disponibilidad",
  "createdAt": "2025-10-03T11:00:00"
}
```

---

## 📊 Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Petición exitosa |
| 201 | Created - Recurso creado exitosamente |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Autenticación requerida |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

### Ejemplo de Error (400)

```json
{
  "timestamp": "2025-10-03T11:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El email ya está registrado",
  "path": "/api/users"
}
```

---

## ✅ Buenas Prácticas

1. **Commits descriptivos**: Usar conventional commits (`feat:`, `fix:`, `docs:`)
2. **Documentación**: Anotar cada endpoint con `@Operation` de Swagger
3. **Validación**: Usar `@Valid` y anotaciones JSR-303
4. **Manejo de errores**: Implementar `@ControllerAdvice` para errores globales
5. **Testing**: Mantener cobertura mínima del 80%
6. **Seguridad**: Nunca exponer contraseñas en logs o responses

---

## ⚙️ Configuración

### Variables de Entorno

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=reservation_db
DB_USER=postgres
DB_PASSWORD=secret

# Security
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### application.yml (alternativo)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: ${SERVER_PORT:8080}
```

---

## 📌 Notas

- Esta documentación está orientada a **entorno de desarrollo**
- **Actualizar configuración de seguridad** antes de desplegar en producción
- Swagger UI refleja únicamente los endpoints y modelos actuales
- Para contribuir, revisar [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

---

## 👥 Contribuidores

- **Tu Nombre** - *Desarrollo inicial* - [@tu-usuari0](https://github.com/Rodrigo-Salva)

---

## 📞 Soporte

¿Problemas o sugerencias? Abre un [issue](https://github.com/tu-usuario/system-reservation-back/issues) en GitHub.

---

**Hecho con ❤️ usando Spring Boot**

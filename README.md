# 📦 Servicio Catálogo de Productos

Este microservicio permite gestionar un catálogo de productos, ofreciendo operaciones CRUD completas y documentación automática mediante Swagger.

---

## 🚀 Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **H2 Database (memoria)**
- **Swagger/OpenAPI (springdoc-openapi)**
- **Lombok**
- **Gradle**

---

## 📂 Estructura del proyecto

- `controller` – Exposición de endpoints REST (`/api/products`)
- `service` – Lógica de negocio
- `repository` – Acceso a datos con Spring Data JPA
- `model` – Entidad `Product`
- `resources/application.yml` – Configuración de base de datos, API Key, etc.

---

## ⚙️ Configuración por defecto

Archivo: `src/main/resources/application.yml`

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:h2:mem:catalogodb
    driverClassName: org.h2.Driver
    username: sa
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
internal:
  api-key: A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6
```

---

## 🧪 Endpoints disponibles

| Método | Endpoint             | Descripción                    |
|--------|----------------------|--------------------------------|
| GET    | `/api/products`      | Listar todos los productos     |
| GET    | `/api/products/{id}` | Obtener producto por ID        |
| POST   | `/api/products`      | Crear nuevo producto           |
| PUT    | `/api/products/{id}` | Actualizar producto existente  |
| DELETE | `/api/products/{id}` | Eliminar producto por ID       |

---

## 🔐 Seguridad

El servicio puede protegerse usando una API Key mediante cabecera HTTP `X-API-KEY`.  
Esto se configura desde `application.yml` bajo la propiedad `internal.api-key`.

---

## 📚 Swagger / OpenAPI

Una vez en ejecución, puedes acceder a la documentación Swagger en:

```
http://localhost:8081/swagger-ui.html
```

También puedes ver la especificación JSON en:

```
http://localhost:8081/v3/api-docs
```

---

## 🧰 Requisitos para compilar

- JDK 17+
- Gradle 8+
- Editor o IDE (IntelliJ, VSCode, Eclipse)

---

## ▶️ Cómo ejecutar el proyecto

```bash
./gradlew bootRun
```

---

## 🧪 Consola H2 (opcional)

Puedes acceder a la consola de H2 para visualizar los datos en memoria:

```
http://localhost:8081/h2-console
```

- JDBC URL: `jdbc:h2:mem:catalogodb`
- Usuario: `sa`

---

## 👨‍💻 Autor

Desarrollado por **Daniel Saraza**  
GitHub: [@dsaraza93](https://github.com/dsaraza93)

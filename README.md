# TP3 · Autenticación, Autorización, Pruebas Automatizadas y Despliegue

Este proyecto corresponde al **TP3 de Desarrollo Web 2**. Amplía el TP2 incorporando:
- **Autenticación stateless** mediante tokens JWT (firmados con HMAC-SHA256).
- **Autorización por rol y control de propiedad de recursos** (`USER` vs `ADMIN`).
- **Pruebas automatizadas piramidales:** pruebas unitarias con Mockito (Maven Surefire) y pruebas de integración con Testcontainers y PostgreSQL real (Maven Failsafe).
- **Pipeline de Integración Continua (CI)** con GitHub Actions.
- **Despliegue en la nube** en Render con base de datos PostgreSQL remota y configuración externalizada mediante variables de entorno.
- **Documentación interactiva de la API** mediante Swagger UI / OpenAPI 3.

---

## Despliegue en Vivo (Render)

- **URL Base:** [https://demo-tp3.onrender.com](https://demo-tp3.onrender.com)
- **Endpoint de Salud (GET /health):** [https://demo-tp3.onrender.com/health](https://demo-tp3.onrender.com/health)
- **Documentación Interactiva (Swagger UI):** [https://demo-tp3.onrender.com/swagger-ui/index.html](https://demo-tp3.onrender.com/swagger-ui/index.html)

---

## Cómo levantar el proyecto localmente

**Requisitos:** Java 25 y Docker (recomendado).

### 1. Base de datos local (Docker Compose)

```bash
docker compose up -d
```
Levanta una instancia de PostgreSQL 17 en el puerto `5432` con la base `webii_tp2` y usuario `webii_tp2`.

### 2. Levantar la aplicación

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```
La aplicación iniciará en `http://localhost:8080`.

---

## Cómo correr los tests

El proyecto sigue una estrategia piramidal de pruebas separando tests unitarios y de integración según su sufijo de nomenclatura y el plugin de Maven que los ejecuta:

### 1. Pruebas Unitarias (`*Test`)
Son ejecutadas por el **Maven Surefire Plugin** durante la fase `test`. Son pruebas aisladas, ultrarrápidas y sin dependencias externas gracias al uso de mocks con Mockito:

```bash
# Windows
.\mvnw.cmd test

# macOS/Linux
./mvnw test
```

- **`ListaServiceImplTest`**:
  - `crear_usuarioAutenticado_asociaListaAUsuario`: verifica que la lista creada queda asociada al ID del usuario autenticado en el contexto de seguridad.
  - `obtener_listaAjena_lanzaAccesoDenegado`: verifica que un usuario con rol `USER` no puede acceder a una lista de otro usuario (lanza `AccesoDenegadoException`, retornando HTTP 403 Forbidden).
  - `obtener_adminAccedeListaAjena_devuelveLista`: verifica que un usuario con rol `ADMIN` sí puede acceder a listas ajenas.
  - `obtener_listaInexistente_lanzaRecursoNoEncontrado`: verifica que al solicitar una lista inexistente se lanza `RecursoNoEncontradoException` (HTTP 404 Not Found).
  - `eliminar_listaPropia_eliminaCorrectamente`: verifica que el dueño de una lista vacía puede eliminarla correctamente.
- **`UsuarioServiceImplTest`**:
  - `registrar_contraseñaTextoPlano_nuncaGuardaEnTextoPlano`: comprueba que las contraseñas nunca se persisten en texto plano y que son hasheadas mediante `PasswordEncoder` (BCrypt).
  - `registrar_emailExistente_lanzaEmailDuplicado`: valida el control de duplicación de correos electrónicos (lanza `EmailDuplicadoException`, HTTP 409 Conflict).
- **`DatabaseUrlEnvironmentPostProcessorTest`**:
  - Valida la conversión y extracción correcta de credenciales y host desde URLs en formato `postgres://` (Render) a formato JDBC estándar `jdbc:postgresql://`.

### 2. Pruebas de Integración (`*IT`)
Son ejecutadas por el **Maven Failsafe Plugin** durante la fase `verify`. Utilizan **Testcontainers** para instanciar dinámicamente un contenedor Docker real de PostgreSQL 17 sobre el cual **Flyway** ejecuta todas las migraciones (`V1` a `V6`):

```bash
# Windows
.\mvnw.cmd verify

# macOS/Linux
./mvnw verify
```
*(Nota: Requiere tener Docker en ejecución).*

- **`ListaControllerIT`**:
  - `login_credencialesValidas_devuelve200ConToken`: valida el flujo completo de autenticación y emisión de token JWT.
  - `login_credencialesInvalidas_devuelve401`: valida el rechazo ante credenciales incorrectas (HTTP 401 Unauthorized).
  - `listar_sinToken_devuelve401`: valida que los endpoints protegidos rechazan solicitudes sin cabecera `Authorization`.
  - `acceder_listaPropia_devuelve200`: comprueba el acceso permitido a recursos propios.
  - `acceder_listaAjena_devuelve403`: comprueba que un usuario autenticado pero no dueño recibe HTTP 403 Forbidden.
  - `acceder_adminAccedeListaAjena_devuelve200`: valida que el rol `ADMIN` tiene permisos globales sobre los recursos.

---

## Cómo leer el resultado de Integración Continua (CI)

El proyecto cuenta con un flujo automatizado de CI configurado en [`.github/workflows/ci.yml`](file:///.github/workflows/ci.yml) que se dispara automáticamente ante cada `push` o `pull_request` a la rama `tp3` o `main`.

### Pasos ejecutados por el pipeline:
1. **Ambiente virtual:** Inicia una máquina virtual limpia con `ubuntu-latest`.
2. **Setup de Java:** Configura Eclipse Temurin JDK 25 con caché automática de dependencias Maven.
3. **Permisos del wrapper:** Otorga permisos de ejecución a `./mvnw` (`chmod +x mvnw`).
4. **Ejecución integral:** Ejecuta `./mvnw -B verify`:
   - Compila todo el código fuente.
   - Ejecuta las pruebas unitarias (`*Test`).
   - Levanta el demonio Docker de GitHub Actions, inicializa el contenedor Testcontainers de PostgreSQL y corre las pruebas de integración (`*IT`).
5. **Reportes de diagnóstico:** En caso de producirse algún fallo, el pipeline ejecuta el paso *"Print test reports on failure"* mostrando directamente en la consola los reportes generados por Surefire y Failsafe.

### Cómo interpretar el resultado en GitHub:
1. Ingresá a la pestaña **Actions** en el repositorio: [https://github.com/martinamoneo/Web2/actions](https://github.com/martinamoneo/Web2/actions).
2. Verificá el estado del commit más reciente:
   - **Tilde verde (Success):** Todos los pasos compilaron, los 11 tests unitarios pasaron y los 6 tests de integración con base de datos real fueron superados con 0 errores y 0 fallos. El código es estable para producción.
   - **Cruz roja (Failure):** Ocurrió un error de compilación o falló una aserción en los tests. Hacé clic en la ejecución fallida y desplegá el paso `Build and run tests with verify` o `Print test reports on failure` para leer la traza exacta del error.

---

## Consigna 7 · Pasos de Despliegue en Render que se siguieron

Para el despliegue en la nube se seleccionó la plataforma **Render** conectada al repositorio de GitHub:

### Paso 1: Creación de la Base de Datos Remota
1. En el Dashboard de Render, se seleccionó **New +** > **PostgreSQL**.
2. Parámetros asignados:
   - Nombre del servicio: `tp3-postgres`
   - Database: `webii_tp3`
   - User: `webii_tp3`
   - Plan: **Free** (PostgreSQL 16)
3. Una vez creada, Render generó la URL de conexión interna y las credenciales correspondientes.

### Paso 2: Creación y Configuración del Servicio Web
1. En Render, se seleccionó **New +** > **Web Service** y se vinculó el repositorio de GitHub en la rama `tp3`.
2. Configuración del build y entorno:
   - **Runtime:** `Docker` (utilizando el [`Dockerfile`](file:///demo-tp3/Dockerfile) multi-stage con imagen `eclipse-temurin:25-jdk` para compilación y `eclipse-temurin:25-jre` para ejecución ligera).
   - **Plan:** Free.

### Paso 3: Configuración de Variables de Entorno
En la sección **Environment** del servicio web se configuraron las siguientes variables clave para desacoplar el entorno de ejecución del código:

| Variable de Entorno | Origen / Configuración en Render | Descripción |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Vinculada desde `tp3-postgres` (`connectionString`) | Cadena de conexión al PostgreSQL remoto |
| `SPRING_DATASOURCE_USERNAME` | Vinculada desde `tp3-postgres` (`user`) | Usuario de la base remota |
| `SPRING_DATASOURCE_PASSWORD` | Vinculada desde `tp3-postgres` (`password`) | Contraseña del usuario de la base remota |
| `JWT_SECRET` | Valor generado aleatoriamente (256 bits) | Clave secreta para firma criptográfica HS256 |
| `JWT_EXPIRATION` | `86400000` | Tiempo de vida del token (24 horas) |
| `PORT` | `8080` | Puerto en el que escucha el servidor Tomcat embebido |

> **Nota de compatibilidad:** Como Render entrega la variable de conexión en formato URI tipo `postgres://user:pass@host:port/db`, se implementó en la aplicación el procesador [`DatabaseUrlEnvironmentPostProcessor`](file:///demo-tp3/src/main/java/com/example/demo/config/DatabaseUrlEnvironmentPostProcessor.java), el cual intercepta el entorno al arrancar y la transforma automáticamente en una URL JDBC compatible (`jdbc:postgresql://...`) extrayendo y asignando las credenciales de forma transparente.

### Paso 4: Ejecución de Migraciones de Flyway
Durante el primer arranque en Render, Spring Boot ejecutó automáticamente **Flyway**, el cual detectó la base de datos remota limpia y aplicó secuencialmente las 6 migraciones del proyecto:
- `V1__create_favoritos.sql`
- `V2__create_listas.sql`
- `V3__add_lista_id_to_favoritos.sql`
- `V4__backfill_lista_defecto.sql`
- `V5__create_usuarios.sql`
- `V6__add_usuario_id_a_listas.sql`

---

## Verificación y Evidencias de Funcionamiento

### 1. Panel de Render (Servicio en estado Live)
El servicio web y la base de datos se encuentran activos y operativos:

![Servicio demo-tp3 en estado Live en Render](docs-utils/img/render-live.png)

### 2. Verificación del Endpoint de Salud (`GET /health`)
Petición HTTP pública directa al endpoint de salud desde el navegador:

![Respuesta pública de GET /health](docs-utils/img/render-health.png)

Respuesta JSON obtenida (HTTP 200 OK):
```json
{
  "status": "ok",
  "service": "demo"
}
```

### 3. Confirmación de Migraciones en Logs de Render
```text
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "1 - create favoritos"
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "2 - create listas"
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "3 - add lista id to favoritos"
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "4 - backfill lista defecto"
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "5 - create usuarios"
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "6 - add usuario id a listas"
o.f.core.internal.command.DbMigrate      : Successfully applied 6 migrations to schema "public"
```

### 4. Confirmación de Arranque Limpio
```text
[DatabaseUrlEnvironmentPostProcessor] Successfully configured JDBC URL: jdbc:postgresql://dpg-...-a.oregon-postgres.render.com:5432/webii_tp3
...
Tomcat started on port 8080 (http) with context path '/'
Started DemoApplication in 5.341 seconds (process running for 6.12)
```

---

## Documentación de la API (Swagger UI / OpenAPI)

La API cuenta con documentación interactiva generada con OpenAPI 3 y Swagger UI:
- **En Render (Producción):** [https://demo-tp3.onrender.com/swagger-ui/index.html](https://demo-tp3.onrender.com/swagger-ui/index.html)
- **En Local:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

En la interfaz de Swagger UI se incluye el botón **"Authorize"** para autenticarse ingresando el token obtenido en `/api/auth/login`.

### Resumen de Endpoints:

| Método | Path | Autenticación | Descripción |
|---|---|---|---|
| POST | `/api/auth/registro` | Pública | Registro de nuevos usuarios (retorna 201 Created o 409 Conflict si ya existe) |
| POST | `/api/auth/login` | Pública | Autenticación con email/password y retorno del token JWT (200 OK o 401 si falla) |
| GET | `/health` | Pública | Chequeo de salud del servicio (`{"status":"ok","service":"demo"}`) |
| GET | `/ping` | Pública | Endpoint simple que retorna `pong` |
| GET | `/api/productos` | Pública | Catálogo de productos paginado (consumido de DummyJSON) |
| GET | `/api/productos/{id}` | Pública | Detalle de un producto individual |
| GET | `/api/favoritos` | **Bearer JWT** | Listar favoritos del usuario |
| POST | `/api/favoritos` | **Bearer JWT** | Crear un nuevo favorito |
| GET | `/api/favoritos/{id}` | **Bearer JWT** | Obtener un favorito por ID (404 si no existe) |
| PUT | `/api/favoritos/{id}` | **Bearer JWT** | Actualizar comentario o lista de un favorito |
| DELETE | `/api/favoritos/{id}` | **Bearer JWT** | Eliminar un favorito existente |
| GET | `/api/listas` | **Bearer JWT** | Listar listas propias (o todas si se tiene rol `ADMIN`) |
| POST | `/api/listas` | **Bearer JWT** | Crear una nueva lista asignada al usuario autenticado |
| GET | `/api/listas/{id}` | **Bearer JWT** | Ver detalle de una lista (403 si pertenece a otro usuario y no es `ADMIN`) |
| PUT | `/api/listas/{id}` | **Bearer JWT** | Modificar nombre o descripción de una lista propia |
| DELETE | `/api/listas/{id}` | **Bearer JWT** | Eliminar una lista vacía (409 Conflict si contiene favoritos) |
| POST | `/api/listas/{origenId}/mover-favoritos` | **Bearer JWT** | Mover favoritos y borrar lista origen en una transacción atómica |
| GET | `/api/listas/{id}/favoritos` | **Bearer JWT** | Obtener favoritos de una lista propia (o cualquiera si `ADMIN`) |

---

## Justificaciones Teóricas del Trabajo Práctico

### Paso 3: Migración a JPA y Arquitectura Hexagonal
Al migrar la persistencia desde la memoria hacia PostgreSQL usando JPA, pudimos observar en la práctica los beneficios de la **Arquitectura Hexagonal**.

- **Qué se modificó (Infraestructura / Adapters):** Se crearon migraciones SQL, la entidad `FavoritoEntity`, el repositorio `FavoritoJpaRepository` y un **Adapter** (`FavoritoRepositoryAdapter`). Se eliminó `InMemoryFavoritoRepository`.
- **Qué NO se modificó (Core / Dominio / Controllers):** El controlador (`FavoritoController`), el servicio (`FavoritoService`), el modelo (`Favorito`) y la interfaz puerto (`FavoritoRepository`) quedaron intactos.

**¿Por qué?** Porque el núcleo de la aplicación se aísla de la infraestructura. El servicio solo conoce el "puerto" (`FavoritoRepository`) y no le importa dónde se guardan los datos. Mientras el adaptador cumpla ese contrato, el cambio de persistencia es transparente.

### Paso 6: Evolución del Esquema (Migraciones)
Al alterar una tabla existente (ej. hacer `lista_id NOT NULL`), se debe crear una **nueva migración (ej. V4)** en lugar de modificar las anteriores. Flyway lleva un registro mediante checksums; si modificamos un archivo viejo, detectará la manipulación y abortará. Agregar migraciones garantiza una historia lineal, inmutable y predecible de la base de datos en todos los entornos.

### Paso 7: Uso de @Transactional (Propiedad ACID)
Al "mover favoritos de una lista a otra y luego borrar la lista original", realizamos múltiples escrituras (UPDATE a favoritos y DELETE a la lista). El uso de `@Transactional` garantiza la **Atomicidad** (la A de ACID): todas las operaciones se tratan como un bloque indivisible. Si ocurriera un error en el medio, se realiza un *rollback* completo, evitando que la base de datos quede en un estado inconsistente (como una lista vacía "zombie").

### TP3 - Autorización: Propiedad de recursos y control de acceso por rol

**¿Dónde vive la regla de autorización?**
La regla de validación de propiedad de recursos y rol (`USER` vs `ADMIN`) vive en la capa de servicios (`ListaServiceImpl`), es decir, en la lógica de negocio de la aplicación.

**¿Por qué se eligió implementarla en el Service y no en el Controller?**
1. **Lógica de dominio vs. transporte (Arquitectura Hexagonal):** Determinar a quién pertenece una lista y si la operación está permitida según el rol del usuario autenticado es una regla de negocio intrínseca. El Controller es un adaptador primario cuya única responsabilidad es transformar pedidos HTTP y delegar en el servicio.
2. **Reutilización y consistencia:** Si el día de mañana se agrega otro punto de entrada (por ejemplo, mensajes por cola, eventos, GraphQL, gRPC o comandos de consola), las reglas de autorización seguirán plenamente garantizadas en el Service sin duplicar código ni depender de la capa web.
3. **Semántica de errores clara (404 vs 403):** El servicio busca primero la entidad (`buscarOFallar`). Si la lista no existe, lanza `RecursoNoEncontradoException` (404 Not Found). Si existe pero pertenece a otro usuario y quien consulta no es `ADMIN`, lanza `AccesoDenegadoException` (403 Forbidden). De esta manera se evita filtrar información o confundir un recurso inexistente con uno prohibido.
4. **Testabilidad:** Permite verificar la regla de autorización en pruebas unitarias aisladas con mocks, sin la sobrecarga de levantar la infraestructura web ni filtros HTTP.

# TP2 · Persistencia, migraciones y arquitectura hexagonal

Este proyecto implementa persistencia con PostgreSQL, JPA y Flyway, siguiendo los principios de la **Arquitectura Hexagonal**.

## Cómo levantar el proyecto

**Requisitos:** Java 25 y Docker (recomendado).

### 1. Base de datos (Docker)

```bash
docker compose up -d
```
Levanta PostgreSQL en el puerto `5432` (base `webii_tp2`, usuario `webii_tp2`).

> *Si no usas Docker, creá la base y el rol manualmente en tu PostgreSQL local y ajustá los datos en `application.properties`.*

### 2. Levantar la aplicación

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```
La aplicación estará disponible en `http://localhost:8080`.
*(Para compilar y correr los tests: `./mvnw test` o `.\mvnw.cmd test`)*

## Endpoints

| Método | Path | Descripción |
|---|---|---|
| GET | `/health` | Chequeo de salud básico |
| GET | `/ping` | Devuelve `pong` (sin JSON) |
| GET | `/api/productos` | Catálogo de productos (consumido de DummyJSON) |
| GET | `/api/productos/{id}` | Un producto puntual |
| GET / POST | `/api/favoritos` | Listar / crear favoritos |
| GET / PUT / DELETE | `/api/favoritos/{id}` | Obtener, actualizar o eliminar un favorito |
| GET / POST / DELETE | `/api/listas` | Endpoints correspondientes a las listas |

> **Documentación interactiva (Swagger UI):** http://localhost:8080/swagger-ui/index.html

---

## Justificaciones del Trabajo Práctico

### Paso 3: Migración a JPA y Arquitectura Hexagonal
Al migrar la persistencia desde la memoria hacia PostgreSQL usando JPA, pudimos observar en la práctica los beneficios de la **Arquitectura Hexagonal**.

- **Qué se modificó (Infraestructura / Adapters):** Se crearon migraciones SQL, la entidad `FavoritoEntity`, el repositorio `FavoritoJpaRepository` y un **Adapter** (`FavoritoRepositoryAdapter`). Se eliminó `InMemoryFavoritoRepository`.
- **Qué NO se modificó (Core / Dominio / Controllers):** El controlador (`FavoritoController`), el servicio (`FavoritoService`), el modelo (`Favorito`) y la interfaz (`FavoritoRepository`) quedaron intactos.

**¿Por qué?** Porque el núcleo de la aplicación se aísla de la infraestructura. El servicio solo conoce el "puerto" (`FavoritoRepository`) y no le importa dónde se guardan los datos. Mientras el adaptador cumpla ese contrato, el cambio de base de datos es totalmente transparente.

### Paso 6: Evolución del Esquema (Migraciones)
Al alterar una tabla existente (ej. hacer `lista_id NOT NULL`), se debe crear una **nueva migración (ej. V4)** en lugar de modificar las anteriores. Flyway lleva un registro mediante checksums; si modificamos un archivo viejo, detectará la manipulación y abortará. Agregar migraciones garantiza una historia lineal, inmutable y predecible de la base de datos en todos los entornos.

### Paso 7: Uso de @Transactional (Propiedad ACID)
Al "mover favoritos de una lista a otra y luego borrar la lista original", realizamos múltiples escrituras (UPDATE a favoritos y DELETE a la lista). El uso de `@Transactional` garantiza la **Atomicidad** (la A de ACID): todas las operaciones se tratan como un bloque indivisible. Si ocurriera un error en el medio, se realiza un *rollback* completo, evitando que la base de datos quede en un estado inconsistente (como una lista vacía "zombie").

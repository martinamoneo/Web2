# TP1 · Spring Boot, API REST y arquitectura en capas

Proyecto finalizado con la resolución de los requerimientos de la consigna.

## Cómo levantar el proyecto

Requiere **Java 25**. Usar el wrapper incluido:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

La app quedará escuchando en `http://localhost:8080`.

## Swagger UI (Documentación)

Todos los endpoints están documentados de forma interactiva en:  
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

## Pruebas (Casos de Éxito y Error)

En la raíz del proyecto se encuentra el archivo **`api-tests.http`** con los casos de éxito y de error solicitados para `/api/productos` y `/api/favoritos`.

Se puede ejecutar fácilmente usando extensiones como **REST Client** en VS Code o el HTTP Client de IntelliJ.

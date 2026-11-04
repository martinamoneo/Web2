package com.example.demo.controller;

import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas", description = "Endpoints para administrar listas de favoritos. [REQUIERE AUTENTICACIÓN: Token Bearer JWT. Usuarios normales solo acceden a sus listas; usuarios ADMIN pueden acceder a todas]")
@SecurityRequirement(name = "BearerAuth")
public class ListaController {

    private final ListaService service;

    public ListaController(ListaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar listas de favoritos", description = "Requiere autenticación. Devuelve las listas creadas por el usuario autenticado (o todas si tiene rol ADMIN).")
    public List<ListaResponse> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva lista", description = "Requiere autenticación. Asocia automáticamente la nueva lista al usuario autenticado.")
    public ListaResponse crear(@Valid @RequestBody ListaRequest request) {
        return service.crear(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una lista por su ID", description = "Requiere autenticación. Retorna 403 Forbidden si la lista pertenece a otro usuario y no es ADMIN. Retorna 404 si no existe.")
    public ListaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar el nombre o descripción de una lista", description = "Requiere autenticación y ser dueño de la lista (o ADMIN).")
    public ListaResponse actualizar(@PathVariable Long id, @Valid @RequestBody ListaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una lista vacía", description = "Requiere autenticación y ser dueño (o ADMIN). Falla con 409 Conflict si la lista contiene favoritos.")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PostMapping("/{origenId}/mover-favoritos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mover todos los favoritos de una lista a otra y eliminar la de origen", description = "Operación transaccional ACID (@Transactional). Requiere autenticación y que ambas listas pertenezcan al usuario autenticado (o ADMIN).")
    public void moverFavoritos(@PathVariable Long origenId, @RequestParam Long destinoId) {
        service.moverFavoritos(origenId, destinoId);
    }

    @GetMapping("/{id}/favoritos")
    @Operation(summary = "Obtener todos los favoritos de una lista", description = "Requiere autenticación y ser dueño de la lista (o ADMIN).")
    public List<FavoritoResponse> obtenerFavoritos(@PathVariable Long id) {
        return service.obtenerFavoritosDeLista(id);
    }
}

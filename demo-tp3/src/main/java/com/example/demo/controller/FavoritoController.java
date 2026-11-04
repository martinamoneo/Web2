package com.example.demo.controller;

import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * CRUD de favoritos persistidos en PostgreSQL mediante JPA.
 * Requiere autenticación con token Bearer JWT.
 */
@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Endpoints para administrar favoritos. [REQUIERE AUTENTICACIÓN: Token Bearer JWT]")
@SecurityRequirement(name = "BearerAuth")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Requiere autenticación con Bearer Token JWT.")
    public List<FavoritoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un favorito por id", description = "Responde 404 si no existe. Requiere autenticación con Bearer Token JWT.")
    public FavoritoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear un favorito", description = "Agrega un producto a favoritos. Requiere autenticación con Bearer Token JWT.")
    public ResponseEntity<FavoritoResponse> crear(@Valid @RequestBody FavoritoRequest request) {
        FavoritoResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/favoritos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un favorito", description = "Actualiza el comentario o lista de un favorito. Responde 404 si no existe. Requiere autenticación con Bearer Token JWT.")
    public FavoritoResponse actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un favorito", description = "Elimina un favorito existente. Responde 404 si no existe. Requiere autenticación con Bearer Token JWT.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

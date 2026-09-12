package com.example.demo.controller;

import com.example.demo.dto.favorito.FavoritoRequestDTO;
import com.example.demo.dto.favorito.FavoritoResponseDTO;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST con el CRUD completo de favoritos.
 * Cada operación usa el método HTTP y el código de estado correctos.
 */
@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "CRUD de productos favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    /** POST /api/favoritos → 201 Created */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un favorito",
            description = "Agrega un producto externo a la lista de favoritos.")
    public FavoritoResponseDTO crear(@Valid @RequestBody FavoritoRequestDTO dto) {
        return favoritoService.crear(dto);
    }

    /** GET /api/favoritos → 200 OK */
    @GetMapping
    @Operation(summary = "Listar todos los favoritos",
            description = "Devuelve la lista completa de favoritos almacenados.")
    public List<FavoritoResponseDTO> listarTodos() {
        return favoritoService.listarTodos();
    }

    /** GET /api/favoritos/{id} → 200 OK */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un favorito por ID",
            description = "Devuelve un único favorito por su identificador interno.")
    public FavoritoResponseDTO obtenerPorId(
            @Parameter(description = "ID del favorito")
            @PathVariable Long id) {
        return favoritoService.obtenerPorId(id);
    }

    /** PUT /api/favoritos/{id} → 200 OK */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un favorito",
            description = "Reemplaza los datos de un favorito existente.")
    public FavoritoResponseDTO actualizar(
            @Parameter(description = "ID del favorito a actualizar")
            @PathVariable Long id,
            @Valid @RequestBody FavoritoRequestDTO dto) {
        return favoritoService.actualizar(id, dto);
    }

    /** DELETE /api/favoritos/{id} → 204 No Content */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un favorito",
            description = "Elimina un favorito por su identificador.")
    public void eliminar(
            @Parameter(description = "ID del favorito a eliminar")
            @PathVariable Long id) {
        favoritoService.eliminar(id);
    }
}

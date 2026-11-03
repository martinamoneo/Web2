package com.example.demo.controller;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas", description = "Endpoints para administrar listas de favoritos")
public class ListaController {
    private final ListaService service;

    public ListaController(ListaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas las listas")
    public List<ListaResponse> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva lista")
    public ListaResponse crear(@Valid @RequestBody ListaRequest request) {
        return service.crear(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una lista por su ID")
    public ListaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar el nombre o descripción de una lista")
    public ListaResponse actualizar(@PathVariable Long id, @Valid @RequestBody ListaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una lista (falla con 409 si la lista no está vacía)")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PostMapping("/{origenId}/mover-favoritos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mover todos los favoritos de una lista a otra y eliminar la lista de origen")
    public void moverFavoritos(@PathVariable Long origenId, @RequestParam Long destinoId) {
        service.moverFavoritos(origenId, destinoId);
    }

    @GetMapping("/{id}/favoritos")
    @Operation(summary = "Obtener todos los favoritos que pertenecen a una lista")
    public List<FavoritoResponse> obtenerFavoritos(@PathVariable Long id) {
        return service.obtenerFavoritosDeLista(id);
    }
}

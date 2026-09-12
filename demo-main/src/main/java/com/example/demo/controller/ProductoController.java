package com.example.demo.controller;

import com.example.demo.dto.PaginaDTO;
import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST del catálogo de productos.
 * Expone los datos de DummyJSON ya transformados a nuestro DTO propio
 * y con paginación amigable (page/size en vez de limit/skip).
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de productos (datos de DummyJSON)")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Lista productos del catálogo con paginación.
     *
     * @param pagina  número de página (empieza en 0, default 0)
     * @param tamanio cantidad de productos por página (default 10)
     * @return página con los productos y metadatos de paginación
     */
    @GetMapping
    @Operation(summary = "Listar productos paginados",
            description = "Devuelve una página de productos del catálogo externo. "
                    + "Se puede controlar la paginación con los parámetros 'pagina' y 'tamanio'.")
    public PaginaDTO<ProductoDTO> listarProductos(
            @Parameter(description = "Número de página (empieza en 0)")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Cantidad de productos por página")
            @RequestParam(defaultValue = "10") int tamanio) {

        return productoService.listarProductos(pagina, tamanio);
    }

    /**
     * Obtiene un producto por su id.
     *
     * @param id identificador del producto
     * @return el producto mapeado a nuestro DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID",
            description = "Devuelve un único producto del catálogo externo por su identificador.")
    public ProductoDTO obtenerProductoPorId(
            @Parameter(description = "ID del producto")
            @PathVariable Long id) {

        return productoService.obtenerProductoPorId(id);
    }
}

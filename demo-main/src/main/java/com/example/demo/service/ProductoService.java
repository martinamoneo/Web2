package com.example.demo.service;

import com.example.demo.client.dummyjson.DummyJsonClient;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.client.dummyjson.DummyJsonProductosResponse;
import com.example.demo.dto.PaginaDTO;
import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de negocio del catálogo de productos. Se encarga de:
 * 1. Delegar las llamadas HTTP al {@link DummyJsonClient}.
 * 2. Mapear los objetos crudos de DummyJSON a nuestros DTOs propios.
 * 3. Transformar la paginación (page/size → limit/skip).
 */
@Service
public class ProductoService {

    private final DummyJsonClient dummyJsonClient;

    public ProductoService(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    /**
     * Devuelve una página de productos del catálogo.
     *
     * @param pagina número de página (empieza en 0)
     * @param tamanio cantidad de productos por página
     * @return una {@link PaginaDTO} con los productos mapeados y metadatos de paginación
     */
    public PaginaDTO<ProductoDTO> listarProductos(int pagina, int tamanio) {
        // Convertir page/size a limit/skip de DummyJSON
        int skip = pagina * tamanio;
        int limit = tamanio;

        DummyJsonProductosResponse respuesta = dummyJsonClient.obtenerProductos(limit, skip);

        List<ProductoDTO> productos = respuesta.products()
                .stream()
                .map(this::mapearAProductoDTO)
                .toList();

        int totalPaginas = (int) Math.ceil((double) respuesta.total() / tamanio);

        return new PaginaDTO<>(productos, pagina, tamanio, respuesta.total(), totalPaginas);
    }

    /**
     * Devuelve un único producto del catálogo por su id.
     *
     * @param id identificador del producto
     * @return el producto mapeado a nuestro DTO
     * @throws RecursoNoEncontradoException si DummyJSON devuelve null
     */
    public ProductoDTO obtenerProductoPorId(Long id) {
        DummyJsonProducto producto = dummyJsonClient.obtenerProductoPorId(id);

        if (producto == null) {
            throw new RecursoNoEncontradoException(
                    "No se encontró el producto con id " + id);
        }

        return mapearAProductoDTO(producto);
    }

    /**
     * Mapea un producto crudo de DummyJSON a nuestro DTO propio.
     * Este es el punto central donde elegimos qué campos exponer
     * y cómo nombrarlos.
     */
    private ProductoDTO mapearAProductoDTO(DummyJsonProducto ext) {
        return new ProductoDTO(
                ext.id(),
                ext.title(),
                ext.description(),
                ext.category(),
                ext.brand(),
                ext.price(),
                ext.discountPercentage(),
                ext.stock(),
                ext.rating(),
                ext.thumbnail()
        );
    }
}

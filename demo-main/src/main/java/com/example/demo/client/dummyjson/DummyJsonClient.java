package com.example.demo.client.dummyjson;

import com.example.demo.exception.ServicioExternoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP que encapsula todas las llamadas a la API de DummyJSON.
 * Es la única clase de la aplicación que conoce las rutas y la forma
 * del JSON externo. El resto del sistema trabaja con DTOs propios.
 */
@Component
public class DummyJsonClient {

    private final RestClient restClient;

    public DummyJsonClient(RestClient dummyJsonRestClient) {
        this.restClient = dummyJsonRestClient;
    }

    /**
     * Trae una página de productos de DummyJSON.
     *
     * @param limit  cantidad de productos a traer
     * @param skip   cantidad de productos a saltear desde el inicio
     * @return la respuesta cruda de DummyJSON con la lista y metadatos de paginación
     */
    public DummyJsonProductosResponse obtenerProductos(int limit, int skip) {
        try {
            return restClient.get()
                    .uri("/products?limit={limit}&skip={skip}", limit, skip)
                    .retrieve()
                    .body(DummyJsonProductosResponse.class);
        } catch (RestClientException ex) {
            throw new ServicioExternoException(
                    "Error al obtener productos de DummyJSON", ex);
        }
    }

    /**
     * Trae un único producto de DummyJSON por su id.
     *
     * @param id identificador del producto en DummyJSON
     * @return el producto crudo tal como viene de la API externa
     */
    public DummyJsonProducto obtenerProductoPorId(Long id) {
        try {
            return restClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .body(DummyJsonProducto.class);
        } catch (RestClientException ex) {
            throw new ServicioExternoException(
                    "Error al obtener el producto " + id + " de DummyJSON", ex);
        }
    }
}

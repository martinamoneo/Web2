package com.example.demo.dto;

import java.util.List;

/**
 * DTO genérico de paginación que envuelve cualquier lista de resultados.
 * El cliente recibe siempre la misma estructura predecible:
 * contenido + metadatos de página.
 *
 * @param <T> tipo de cada elemento de la lista
 */
public record PaginaDTO<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas
) {
}

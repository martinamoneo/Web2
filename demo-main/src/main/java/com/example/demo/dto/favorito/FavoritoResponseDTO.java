package com.example.demo.dto.favorito;

import java.time.LocalDateTime;

/**
 * DTO de salida: lo que la API devuelve al cliente cuando consulta
 * un favorito. Incluye todos los campos relevantes, incluidos los
 * que el servidor asigna (id y fecha).
 */
public record FavoritoResponseDTO(
        Long id,
        Long productoExternoId,
        String notaPersonal,
        LocalDateTime fechaAgregado
) {
}

package com.example.demo.dto.favorito;

import com.example.demo.model.Favorito;

import java.time.LocalDateTime;

/**
 * Clase utilitaria que convierte entre la entidad {@link Favorito}
 * y sus DTOs de entrada/salida. El mapeo se hace a mano (sin
 * librerías externas como MapStruct) para mantener el proyecto simple.
 */
public final class FavoritoMapper {

    private FavoritoMapper() {
        // Clase utilitaria — no instanciable
    }

    /**
     * Convierte un DTO de entrada en una entidad de dominio.
     * El id queda en null (lo asigna el repository) y la fecha
     * se fija al momento actual.
     */
    public static Favorito toEntity(FavoritoRequestDTO dto) {
        Favorito favorito = new Favorito();
        favorito.setProductoExternoId(dto.productoExternoId());
        favorito.setNotaPersonal(dto.notaPersonal());
        favorito.setFechaAgregado(LocalDateTime.now());
        return favorito;
    }

    /**
     * Convierte una entidad de dominio en un DTO de salida.
     */
    public static FavoritoResponseDTO toResponseDTO(Favorito favorito) {
        return new FavoritoResponseDTO(
                favorito.getId(),
                favorito.getProductoExternoId(),
                favorito.getNotaPersonal(),
                favorito.getFechaAgregado()
        );
    }
}

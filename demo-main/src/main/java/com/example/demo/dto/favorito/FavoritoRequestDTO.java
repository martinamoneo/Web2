package com.example.demo.dto.favorito;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada: lo que el cliente envía al crear o actualizar un favorito.
 * No incluye id ni fecha — esos los asigna el servidor.
 */
public record FavoritoRequestDTO(

        @NotNull(message = "El id del producto es obligatorio")
        @Positive(message = "El id del producto debe ser positivo")
        Long productoExternoId,

        @Size(max = 255, message = "La nota no puede superar los 255 caracteres")
        String notaPersonal
) {
}

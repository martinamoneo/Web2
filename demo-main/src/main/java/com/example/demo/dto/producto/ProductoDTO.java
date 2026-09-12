package com.example.demo.dto.producto;

/**
 * DTO público del producto que expone nuestra API.
 * Solo incluye los campos que le interesan al cliente; los nombres
 * están en español siguiendo la convención del resto del proyecto.
 * Nunca se expone el JSON crudo de DummyJSON.
 */
public record ProductoDTO(
        Long id,
        String nombre,
        String descripcion,
        String categoria,
        String marca,
        double precio,
        double descuento,
        int stock,
        double valoracion,
        String imagen
) {
}

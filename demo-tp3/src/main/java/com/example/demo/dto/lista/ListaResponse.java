package com.example.demo.dto.lista;

public record ListaResponse(Long id, String nombre, String descripcion, Long usuarioId) {
    public ListaResponse(Long id, String nombre, String descripcion) {
        this(id, nombre, descripcion, null);
    }
}

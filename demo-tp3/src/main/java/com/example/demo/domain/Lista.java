package com.example.demo.domain;

public record Lista(Long id, String nombre, String descripcion, Long usuarioId) {
    public Lista(Long id, String nombre, String descripcion) {
        this(id, nombre, descripcion, null);
    }
}

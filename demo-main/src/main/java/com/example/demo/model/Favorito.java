package com.example.demo.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa un producto marcado como favorito
 * por el usuario. No está ligada a JPA — se persiste en memoria.
 *
 * Guarda una referencia al id del producto externo (DummyJSON),
 * una nota personal opcional y la fecha en que se agregó.
 */
public class Favorito {

    private Long id;
    private Long productoExternoId;
    private String notaPersonal;
    private LocalDateTime fechaAgregado;

    public Favorito() {
    }

    public Favorito(Long id, Long productoExternoId, String notaPersonal, LocalDateTime fechaAgregado) {
        this.id = id;
        this.productoExternoId = productoExternoId;
        this.notaPersonal = notaPersonal;
        this.fechaAgregado = fechaAgregado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoExternoId() {
        return productoExternoId;
    }

    public void setProductoExternoId(Long productoExternoId) {
        this.productoExternoId = productoExternoId;
    }

    public String getNotaPersonal() {
        return notaPersonal;
    }

    public void setNotaPersonal(String notaPersonal) {
        this.notaPersonal = notaPersonal;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}

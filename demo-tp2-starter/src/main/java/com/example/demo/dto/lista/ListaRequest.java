package com.example.demo.dto.lista;
import jakarta.validation.constraints.NotBlank;

public record ListaRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    String descripcion
) {}

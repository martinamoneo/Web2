package com.example.demo.repository;

import com.example.demo.model.Favorito;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de favoritos. Define las operaciones de
 * persistencia sin asumir ninguna tecnología concreta (ni JPA, ni base
 * de datos). La implementación actual es en memoria, pero si mañana
 * se quisiera migrar a una base de datos, solo hay que crear otra
 * implementación de esta interfaz.
 */
public interface FavoritoRepository {

    /** Guarda un favorito (nuevo o existente) y lo devuelve con id asignado. */
    Favorito guardar(Favorito favorito);

    /** Busca un favorito por su id interno. */
    Optional<Favorito> buscarPorId(Long id);

    /** Devuelve todos los favoritos almacenados. */
    List<Favorito> buscarTodos();

    /** Elimina un favorito por su id. Devuelve true si existía. */
    boolean eliminarPorId(Long id);
}

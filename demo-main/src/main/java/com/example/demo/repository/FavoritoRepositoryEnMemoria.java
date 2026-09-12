package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación en memoria del repositorio de favoritos.
 * Usa un {@link ConcurrentHashMap} para ser thread-safe y un
 * {@link AtomicLong} como generador de ids autoincrementales.
 *
 * Los datos se pierden al reiniciar la aplicación — es lo esperado
 * para este TP sin base de datos.
 */
@Repository
public class FavoritoRepositoryEnMemoria implements FavoritoRepository {

    private final Map<Long, Favorito> favoritos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    @Override
    public Favorito guardar(Favorito favorito) {
        if (favorito.getId() == null) {
            favorito.setId(secuencia.getAndIncrement());
        }
        favoritos.put(favorito.getId(), favorito);
        return favorito;
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return Optional.ofNullable(favoritos.get(id));
    }

    @Override
    public List<Favorito> buscarTodos() {
        return new ArrayList<>(favoritos.values());
    }

    @Override
    public boolean eliminarPorId(Long id) {
        return favoritos.remove(id) != null;
    }
}

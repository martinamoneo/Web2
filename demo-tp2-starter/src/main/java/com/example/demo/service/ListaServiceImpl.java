package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.exception.ListaNoVaciaException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaServiceImpl(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    @Override
    public List<ListaResponse> listar() {
        return listaRepository.findAll().stream().map(this::aResponse).toList();
    }

    @Override
    public ListaResponse obtener(Long id) {
        return aResponse(buscarOFallar(id));
    }

    @Override
    public ListaResponse crear(ListaRequest request) {
        Lista nueva = new Lista(null, request.nombre(), request.descripcion());
        return aResponse(listaRepository.save(nueva));
    }

    @Override
    public ListaResponse actualizar(Long id, ListaRequest request) {
        Lista existente = buscarOFallar(id);
        Lista actualizada = new Lista(existente.id(), request.nombre(), request.descripcion());
        return aResponse(listaRepository.save(actualizada));
    }

    @Override
    public void eliminar(Long id) {
        buscarOFallar(id);
        List<Favorito> favoritos = favoritoRepository.findByListaId(id);
        if (!favoritos.isEmpty()) {
            throw new ListaNoVaciaException("No se puede eliminar la lista porque contiene favoritos.");
        }
        listaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void moverFavoritos(Long origenId, Long destinoId) {
        buscarOFallar(origenId);
        buscarOFallar(destinoId);
        
        List<Favorito> favoritos = favoritoRepository.findByListaId(origenId);
        for (Favorito fav : favoritos) {
            Favorito actualizado = new Favorito(fav.id(), fav.productoId(), fav.nota(), fav.fechaAgregado(), destinoId);
            favoritoRepository.save(actualizado);
        }
        
        listaRepository.deleteById(origenId);
    }

    @Override
    public List<FavoritoResponse> obtenerFavoritosDeLista(Long listaId) {
        buscarOFallar(listaId);
        return favoritoRepository.findByListaId(listaId).stream()
                .map(f -> new FavoritoResponse(f.id(), f.productoId(), f.nota(), f.fechaAgregado(), f.listaId()))
                .toList();
    }

    private Lista buscarOFallar(Long id) {
        return listaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la lista con id " + id));
    }

    private ListaResponse aResponse(Lista lista) {
        return new ListaResponse(lista.id(), lista.nombre(), lista.descripcion());
    }
}

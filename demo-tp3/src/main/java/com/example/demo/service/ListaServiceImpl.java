package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.exception.AccesoDenegadoException;
import com.example.demo.exception.ListaNoVaciaException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
        if (esAdmin()) {
            return listaRepository.findAll().stream().map(this::aResponse).toList();
        }
        Long usuarioId = obtenerUsuarioIdAutenticado();
        return listaRepository.findByUsuarioId(usuarioId).stream().map(this::aResponse).toList();
    }

    @Override
    public ListaResponse obtener(Long id) {
        Lista lista = buscarOFallar(id);
        validarPropietarioOAdmin(lista);
        return aResponse(lista);
    }

    @Override
    public ListaResponse crear(ListaRequest request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Lista nueva = new Lista(null, request.nombre(), request.descripcion(), usuarioId);
        return aResponse(listaRepository.save(nueva));
    }

    @Override
    public ListaResponse actualizar(Long id, ListaRequest request) {
        Lista existente = buscarOFallar(id);
        validarPropietarioOAdmin(existente);
        Lista actualizada = new Lista(existente.id(), request.nombre(), request.descripcion(), existente.usuarioId());
        return aResponse(listaRepository.save(actualizada));
    }

    @Override
    public void eliminar(Long id) {
        Lista lista = buscarOFallar(id);
        validarPropietarioOAdmin(lista);
        List<Favorito> favoritos = favoritoRepository.findByListaId(id);
        if (!favoritos.isEmpty()) {
            throw new ListaNoVaciaException("No se puede eliminar la lista porque contiene favoritos.");
        }
        listaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void moverFavoritos(Long origenId, Long destinoId) {
        Lista origen = buscarOFallar(origenId);
        validarPropietarioOAdmin(origen);
        Lista destino = buscarOFallar(destinoId);
        validarPropietarioOAdmin(destino);
        
        List<Favorito> favoritos = favoritoRepository.findByListaId(origenId);
        for (Favorito fav : favoritos) {
            Favorito actualizado = new Favorito(fav.id(), fav.productoId(), fav.nota(), fav.fechaAgregado(), destinoId);
            favoritoRepository.save(actualizado);
        }
        
        listaRepository.deleteById(origenId);
    }

    @Override
    public List<FavoritoResponse> obtenerFavoritosDeLista(Long listaId) {
        Lista lista = buscarOFallar(listaId);
        validarPropietarioOAdmin(lista);
        return favoritoRepository.findByListaId(listaId).stream()
                .map(f -> new FavoritoResponse(f.id(), f.productoId(), f.nota(), f.fechaAgregado(), f.listaId()))
                .toList();
    }

    private void validarPropietarioOAdmin(Lista lista) {
        if (esAdmin()) {
            return;
        }
        Long usuarioIdActual = obtenerUsuarioIdAutenticado();
        if (lista.usuarioId() == null || !Objects.equals(lista.usuarioId(), usuarioIdActual)) {
            throw new AccesoDenegadoException("No tienes permiso para acceder a esta lista");
        }
    }

    private Long obtenerUsuarioIdAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccesoDenegadoException("Usuario no autenticado");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long id) {
            return id;
        }
        String name = auth.getName();
        if (name != null) {
            try {
                return Long.valueOf(name);
            } catch (NumberFormatException ignored) {}
        }
        if (principal instanceof String s) {
            try {
                return Long.valueOf(s);
            } catch (NumberFormatException ignored) {}
        }
        throw new AccesoDenegadoException("Identidad de usuario no válida");
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
    }

    private Lista buscarOFallar(Long id) {
        return listaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la lista con id " + id));
    }

    private ListaResponse aResponse(Lista lista) {
        return new ListaResponse(lista.id(), lista.nombre(), lista.descripcion(), lista.usuarioId());
    }
}

package com.example.demo.service;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.dto.favorito.FavoritoResponse;
import java.util.List;

public interface ListaService {
    List<ListaResponse> listar();
    ListaResponse obtener(Long id);
    ListaResponse crear(ListaRequest request);
    ListaResponse actualizar(Long id, ListaRequest request);
    void eliminar(Long id);
    void moverFavoritos(Long origenId, Long destinoId);
    List<FavoritoResponse> obtenerFavoritosDeLista(Long listaId);
}

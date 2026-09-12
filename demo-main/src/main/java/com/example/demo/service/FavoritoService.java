package com.example.demo.service;

import com.example.demo.dto.favorito.FavoritoMapper;
import com.example.demo.dto.favorito.FavoritoRequestDTO;
import com.example.demo.dto.favorito.FavoritoResponseDTO;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Favorito;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de negocio del módulo de favoritos. Orquesta la conversión
 * DTO ↔ entidad y delega la persistencia al repository.
 */
@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository) {
        this.favoritoRepository = favoritoRepository;
    }

    public FavoritoResponseDTO crear(FavoritoRequestDTO dto) {
        Favorito favorito = FavoritoMapper.toEntity(dto);
        Favorito guardado = favoritoRepository.guardar(favorito);
        return FavoritoMapper.toResponseDTO(guardado);
    }

    public List<FavoritoResponseDTO> listarTodos() {
        return favoritoRepository.buscarTodos()
                .stream()
                .map(FavoritoMapper::toResponseDTO)
                .toList();
    }

    public FavoritoResponseDTO obtenerPorId(Long id) {
        Favorito favorito = favoritoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el favorito con id " + id));
        return FavoritoMapper.toResponseDTO(favorito);
    }

    public FavoritoResponseDTO actualizar(Long id, FavoritoRequestDTO dto) {
        Favorito existente = favoritoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el favorito con id " + id));

        // Actualiza solo los campos que el cliente puede modificar;
        // el id y la fecha original se conservan.
        existente.setProductoExternoId(dto.productoExternoId());
        existente.setNotaPersonal(dto.notaPersonal());

        Favorito guardado = favoritoRepository.guardar(existente);
        return FavoritoMapper.toResponseDTO(guardado);
    }

    public void eliminar(Long id) {
        boolean existia = favoritoRepository.eliminarPorId(id);
        if (!existia) {
            throw new RecursoNoEncontradoException(
                    "No se encontró el favorito con id " + id);
        }
    }
}

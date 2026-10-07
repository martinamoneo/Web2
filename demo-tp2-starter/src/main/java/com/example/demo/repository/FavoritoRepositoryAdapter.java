package com.example.demo.repository;

import com.example.demo.domain.Favorito;
import com.example.demo.entity.FavoritoEntity;
import com.example.demo.entity.ListaEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Favorito> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Favorito save(Favorito favorito) {
        FavoritoEntity entity = toEntity(favorito);
        FavoritoEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Override
    public List<Favorito> findByListaId(Long listaId) {
        return jpaRepository.findByListaId(listaId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    private Favorito toDomain(FavoritoEntity entity) {
        return new Favorito(
                entity.getId(),
                entity.getProductoId(),
                entity.getNota(),
                entity.getFechaAgregado(),
                entity.getLista() != null ? entity.getLista().getId() : null
        );
    }

    private FavoritoEntity toEntity(Favorito domain) {
        FavoritoEntity entity = new FavoritoEntity();
        entity.setId(domain.id());
        entity.setProductoId(domain.productoId());
        entity.setNota(domain.nota());
        entity.setFechaAgregado(domain.fechaAgregado());
        if (domain.listaId() != null) {
            entity.setLista(entityManager.getReference(ListaEntity.class, domain.listaId()));
        }
        return entity;
    }
}

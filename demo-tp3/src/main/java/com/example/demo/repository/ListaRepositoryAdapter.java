package com.example.demo.repository;

import com.example.demo.domain.Lista;
import com.example.demo.entity.ListaEntity;
import com.example.demo.entity.UsuarioEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Lista> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity entity = new ListaEntity();
        entity.setId(lista.id());
        entity.setNombre(lista.nombre());
        entity.setDescripcion(lista.descripcion());
        if (lista.usuarioId() != null) {
            entity.setUsuario(entityManager.getReference(UsuarioEntity.class, lista.usuarioId()));
        }
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    private Lista toDomain(ListaEntity entity) {
        return new Lista(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getUsuario() != null ? entity.getUsuario().getId() : null
        );
    }
}

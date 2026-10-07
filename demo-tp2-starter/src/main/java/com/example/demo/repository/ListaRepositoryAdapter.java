package com.example.demo.repository;
import com.example.demo.domain.Lista;
import com.example.demo.entity.ListaEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ListaRepositoryAdapter implements ListaRepository {
    private final ListaJpaRepository jpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
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
        return new Lista(entity.getId(), entity.getNombre(), entity.getDescripcion());
    }
}

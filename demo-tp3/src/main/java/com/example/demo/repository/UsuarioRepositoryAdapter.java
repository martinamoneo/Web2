package com.example.demo.repository;

import com.example.demo.domain.Usuario;
import com.example.demo.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.id());
        entity.setEmail(usuario.email());
        entity.setPasswordHash(usuario.passwordHash());
        entity.setRol(usuario.rol());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getRol());
    }
}


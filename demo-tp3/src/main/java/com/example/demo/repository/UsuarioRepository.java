package com.example.demo.repository;

import com.example.demo.domain.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
    boolean existsByEmail(String email);
}


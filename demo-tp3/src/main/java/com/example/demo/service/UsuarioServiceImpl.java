package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.auth.RegistroRequest;
import com.example.demo.dto.auth.UsuarioResponse;
import com.example.demo.exception.EmailDuplicadoException;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailDuplicadoException("Ya existe un usuario con el email " + request.email());
        }

        Usuario nuevo = new Usuario(
                null,
                request.email(),
                passwordEncoder.encode(request.password()),
                Usuario.Rol.USER
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        return new UsuarioResponse(guardado.id(), guardado.email(), guardado.rol().name());
    }
}


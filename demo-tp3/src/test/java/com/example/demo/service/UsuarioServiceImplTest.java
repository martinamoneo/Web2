package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.auth.RegistroRequest;
import com.example.demo.dto.auth.UsuarioResponse;
import com.example.demo.exception.EmailDuplicadoException;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void registrar_contraseñaTextoPlano_nuncaGuardaEnTextoPlano() {
        // Arrange
        String passwordPlano = "claveSuperSecreta123";
        String hashEsperado = "$2a$10$e7K9hYf8kG2eL9g1v8WkEu1M4G4dE0P6YqK9x2T3J";
        RegistroRequest request = new RegistroRequest("nuevo@example.com", passwordPlano);

        when(usuarioRepository.existsByEmail("nuevo@example.com")).thenReturn(false);
        when(passwordEncoder.encode(passwordPlano)).thenReturn(hashEsperado);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario u = invocation.getArgument(0);
                    return new Usuario(1L, u.email(), u.passwordHash(), u.rol());
                });

        // Act
        UsuarioResponse response = usuarioService.registrar(request);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario usuarioGuardado = captor.getValue();

        assertNotNull(usuarioGuardado);
        assertNotEquals(passwordPlano, usuarioGuardado.passwordHash(),
                "La contraseña nunca debe guardarse en texto plano en la base de datos");
        assertEquals(hashEsperado, usuarioGuardado.passwordHash(),
                "La contraseña guardada debe coincidir con el hash producido por el PasswordEncoder");
        assertEquals(Usuario.Rol.USER, usuarioGuardado.rol(),
                "El rol por defecto debe ser USER");
        assertEquals("nuevo@example.com", response.email());
        assertEquals("USER", response.rol());
        verify(passwordEncoder).encode(passwordPlano);
    }

    @Test
    void registrar_emailExistente_lanzaEmailDuplicado() {
        // Arrange
        RegistroRequest request = new RegistroRequest("existente@example.com", "password123");
        when(usuarioRepository.existsByEmail("existente@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailDuplicadoException.class, () -> {
            usuarioService.registrar(request);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}


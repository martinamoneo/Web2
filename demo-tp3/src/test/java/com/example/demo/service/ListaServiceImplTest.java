package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.exception.AccesoDenegadoException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    @InjectMocks
    private ListaServiceImpl listaService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(Long usuarioId, String rol) {
        var auth = new UsernamePasswordAuthenticationToken(
                usuarioId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void crear_usuarioAutenticado_asociaListaAUsuario() {
        // Arrange
        Long usuarioIdEsperado = 42L;
        autenticarComo(usuarioIdEsperado, "USER");

        ListaRequest request = new ListaRequest("Mis Celulares", "Lista personal de teléfonos");
        Lista listaGuardada = new Lista(1L, "Mis Celulares", "Lista personal de teléfonos", usuarioIdEsperado);

        when(listaRepository.save(any(Lista.class))).thenReturn(listaGuardada);

        // Act
        ListaResponse response = listaService.crear(request);

        // Assert
        ArgumentCaptor<Lista> captor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).save(captor.capture());
        Lista listaCapturada = captor.getValue();

        assertEquals(usuarioIdEsperado, listaCapturada.usuarioId(),
                "La lista creada debe quedar asociada al id del usuario autenticado");
        assertEquals("Mis Celulares", response.nombre());
        assertEquals(usuarioIdEsperado, response.usuarioId());
    }

    @Test
    void obtener_listaAjena_lanzaAccesoDenegado() {
        // Arrange
        Long usuarioAutenticadoId = 1L;
        Long usuarioDuenioId = 2L;
        autenticarComo(usuarioAutenticadoId, "USER");

        Lista listaAjena = new Lista(10L, "Lista de Otro", "Privada", usuarioDuenioId);
        when(listaRepository.findById(10L)).thenReturn(Optional.of(listaAjena));

        // Act & Assert
        AccesoDenegadoException exception = assertThrows(AccesoDenegadoException.class, () -> {
            listaService.obtener(10L);
        });

        assertTrue(exception.getMessage().contains("No tienes permiso"),
                "Debe denegar el acceso a listas ajenas para usuarios con rol USER");
    }

    @Test
    void obtener_adminAccedeListaAjena_devuelveLista() {
        // Arrange
        Long adminId = 999L;
        Long usuarioDuenioId = 2L;
        autenticarComo(adminId, "ADMIN");

        Lista listaAjena = new Lista(10L, "Lista de Usuario", "Privada", usuarioDuenioId);
        when(listaRepository.findById(10L)).thenReturn(Optional.of(listaAjena));

        // Act
        ListaResponse response = listaService.obtener(10L);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Lista de Usuario", response.nombre());
        assertEquals(usuarioDuenioId, response.usuarioId());
    }

    @Test
    void obtener_listaInexistente_lanzaRecursoNoEncontrado() {
        // Arrange
        autenticarComo(1L, "USER");
        when(listaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            listaService.obtener(99L);
        });
    }

    @Test
    void eliminar_listaPropia_eliminaCorrectamente() {
        // Arrange
        Long usuarioId = 5L;
        autenticarComo(usuarioId, "USER");

        Lista listaPropia = new Lista(15L, "Para borrar", "Vacia", usuarioId);
        when(listaRepository.findById(15L)).thenReturn(Optional.of(listaPropia));
        when(favoritoRepository.findByListaId(15L)).thenReturn(List.of());

        // Act
        listaService.eliminar(15L);

        // Assert
        verify(listaRepository).deleteById(15L);
    }
}


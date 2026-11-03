package com.example.demo.controller;

import com.example.demo.domain.Lista;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
class ListaControllerIT {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void login_credencialesValidas_devuelve200ConToken() throws Exception {
        String email = "login_valido_" + System.currentTimeMillis() + "@example.com";
        String password = "passwordSeguro123";
        usuarioRepository.save(new Usuario(null, email, passwordEncoder.encode(password), Usuario.Rol.USER));

        LoginRequest loginRequest = new LoginRequest(email, password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("no_existe@example.com", "passwordErroneo");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listar_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceder_listaPropia_devuelve200() throws Exception {
        String email = "propia_" + System.currentTimeMillis() + "@example.com";
        Usuario usuario = usuarioRepository.save(new Usuario(
                null, email, passwordEncoder.encode("pass123"), Usuario.Rol.USER));
        String token = jwtService.generarToken(usuario.id(), usuario.email(), usuario.rol().name());

        Lista lista = listaRepository.save(new Lista(null, "Mi Lista Propia", "Descripción", usuario.id()));

        mockMvc.perform(get("/api/listas/" + lista.id())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lista.id()))
                .andExpect(jsonPath("$.nombre").value("Mi Lista Propia"));
    }

    @Test
    void acceder_listaAjena_devuelve403() throws Exception {
        String emailDuenio = "duenio_" + System.currentTimeMillis() + "@example.com";
        String emailIntruso = "intruso_" + System.currentTimeMillis() + "@example.com";

        Usuario duenio = usuarioRepository.save(new Usuario(
                null, emailDuenio, passwordEncoder.encode("pass123"), Usuario.Rol.USER));
        Usuario intruso = usuarioRepository.save(new Usuario(
                null, emailIntruso, passwordEncoder.encode("pass123"), Usuario.Rol.USER));

        Lista listaAjena = listaRepository.save(new Lista(null, "Lista de Otro", "Privada", duenio.id()));
        String tokenIntruso = jwtService.generarToken(intruso.id(), intruso.email(), intruso.rol().name());

        mockMvc.perform(get("/api/listas/" + listaAjena.id())
                        .header("Authorization", "Bearer " + tokenIntruso))
                .andExpect(status().isForbidden());
    }

    @Test
    void acceder_adminAccedeListaAjena_devuelve200() throws Exception {
        String emailDuenio = "duenio_admin_test_" + System.currentTimeMillis() + "@example.com";
        String emailAdmin = "admin_" + System.currentTimeMillis() + "@example.com";

        Usuario duenio = usuarioRepository.save(new Usuario(
                null, emailDuenio, passwordEncoder.encode("pass123"), Usuario.Rol.USER));
        Usuario admin = usuarioRepository.save(new Usuario(
                null, emailAdmin, passwordEncoder.encode("pass123"), Usuario.Rol.ADMIN));

        Lista listaAjena = listaRepository.save(new Lista(null, "Lista para Admin", "Confidencial", duenio.id()));
        String tokenAdmin = jwtService.generarToken(admin.id(), admin.email(), admin.rol().name());

        mockMvc.perform(get("/api/listas/" + listaAjena.id())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listaAjena.id()))
                .andExpect(jsonPath("$.nombre").value("Lista para Admin"));
    }
}

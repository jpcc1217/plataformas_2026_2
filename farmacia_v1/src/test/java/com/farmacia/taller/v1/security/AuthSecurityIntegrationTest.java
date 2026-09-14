package com.farmacia.taller.v1.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.farmacia.taller.v1.repository.UsuarioRepository;
import com.farmacia.taller.v1.repository.MedicamentoRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        medicamentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("1. Acceso a endpoint protegido sin token debe retornar 401 Unauthorized")
    void testAccesoProtegidoSinToken() throws Exception {
        mockMvc.perform(get("/api/v1/medicamentos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("1b. Acceso a endpoint protegido con token invalido debe retornar 401 Unauthorized")
    void testAccesoProtegidoConTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/medicamentos")
                        .header("Authorization", "Bearer token.invalido.corrupto"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("2. Registro de usuario ADMIN debe retornar 200 OK y token JWT")
    void testRegistroAdmin() throws Exception {
        String registerJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("3. Login con credenciales validas debe retornar 200 OK y token")
    void testLoginExitoso() throws Exception {
        String registerJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("4. Login con credenciales invalidas debe retornar 401 UNAUTHORIZED")
    void testLoginCredencialesInvalidas() throws Exception {
        String registerJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String badLoginJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password_incorrecto"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badLoginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas. Por favor verifica tu usuario o contraseña."));
    }

    @Test
    @DisplayName("5. Crear medicamento con token ADMIN debe retornar 201 CREATED")
    void testCrearMedicamentoConAdminToken() throws Exception {
        String registerJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String token = responseNode.get("token").asText();
        assertNotNull(token);

        String medJson = """
                {
                    "nombre": "Amoxicilina 500mg",
                    "fechaExpiracion": "2026-08-18T00:00:00.000+00:00",
                    "precio": 18500.0,
                    "cantidad": 50,
                    "categoriaId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/medicamentos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Amoxicilina 500mg"))
                .andExpect(jsonPath("$.precio").value(18500.0));
    }

    @Test
    @DisplayName("6. Consultar medicamentos con token valido debe retornar 200 OK")
    void testConsultarMedicamentosConToken() throws Exception {
        String registerJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String token = responseNode.get("token").asText();

        mockMvc.perform(get("/api/v1/medicamentos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("7. Usuario con rol USER no puede crear medicamentos (403 Forbidden), pero sí consultar (200 OK)")
    void testRolUserPermisos() throws Exception {
        // Registrar usuario con rol USER
        String registerUserJson = """
                {
                    "username": "cliente_farmacia",
                    "password": "password123"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String userToken = responseNode.get("token").asText();

        // Intento de creacion -> 403 Forbidden
        String medJson = """
                {
                    "nombre": "Ibuprofeno 400mg",
                    "fechaExpiracion": "2026-08-18T00:00:00.000+00:00",
                    "precio": 9500.0,
                    "cantidad": 30,
                    "categoriaId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/medicamentos")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medJson))
                .andExpect(status().isForbidden());

        // Consulta de medicamentos con rol USER -> 200 OK
        mockMvc.perform(get("/api/v1/medicamentos")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("8. Token JWT debe contener claim 'role' con valor ADMIN o USER")
    void testTokenContieneClaimRole() throws Exception {
        String registerJson = """
                {
                    "username": "carlos_admin",
                    "password": "password123"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String token = responseNode.get("token").asText();

        String[] parts = token.split("\\.");
        String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);
        JsonNode payloadNode = objectMapper.readTree(payloadJson);

        org.junit.jupiter.api.Assertions.assertEquals("ADMIN", payloadNode.get("role").asText());
    }

    @Test
    @DisplayName("9. Registrar un username ya existente debe retornar 409 Conflict")
    void testRegistroUsernameDuplicado() throws Exception {
        String registerJson = """
                {
                    "username": "admin_farmacia",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }
}

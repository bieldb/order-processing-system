package com.gabrielalves.order_processing_system.exception.handler;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielalves.order_processing_system.dto.CustomerRequestDTO;
import com.gabrielalves.order_processing_system.exception.EmailAlreadyExistsException;
import com.gabrielalves.order_processing_system.exception.NotFoundException;

import jakarta.validation.Valid;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn404WhenResourceIsNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/test/not-found"))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.fields").doesNotExist());
    }

    @Test
    void shouldReturn409OnBusinessConflict() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um cadastro com esse email: a@a.com"));
    }

    @Test
    void shouldReturn409OnDatabaseConstraintViolation() throws Exception {
        mockMvc.perform(get("/test/integrity"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A operação viola uma restrição de integridade dos dados"));
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"email\": \"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.fields", hasSize(3)));
    }

    @Test
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou malformado"));
    }

    @Test
    void shouldReturn400WhenPathVariableIsNotAValidUuid() throws Exception {
        mockMvc.perform(get("/test/uuid/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Valor inválido para o parâmetro 'id': abc"));
    }

    @Test
    void shouldKeepFrameworkStatusForUnsupportedMethod() throws Exception {
        mockMvc.perform(post("/test/not-found"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.path").value("/test/not-found"));
    }

    @Test
    void shouldReturn500WithoutLeakingInternalDetails() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        void notFound() {
            throw new NotFoundException("Recurso não encontrado");
        }

        @GetMapping("/test/conflict")
        void conflict() {
            throw new EmailAlreadyExistsException("a@a.com");
        }

        @GetMapping("/test/integrity")
        void integrity() {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }

        @GetMapping("/test/unexpected")
        void unexpected() {
            throw new IllegalStateException("detalhe interno que não deve vazar");
        }

        @GetMapping("/test/uuid/{id}")
        void uuid(@PathVariable UUID id) {
        }

        @PostMapping("/test/validation")
        void validation(@RequestBody @Valid CustomerRequestDTO dto) {
        }
    }
}

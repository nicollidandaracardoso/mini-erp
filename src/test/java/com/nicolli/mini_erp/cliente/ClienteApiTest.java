package com.nicolli.mini_erp.cliente;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.nicolli.mini_erp.TesteApiBase;

class ClienteApiTest extends TesteApiBase {

    @Test
    void criarClienteValidoRetorna201() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana Silva","email":"ana@example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Ana Silva"));
    }

    @Test
    void criarClienteComEmailRepetidoRetorna422() throws Exception {
        criarCliente("Ana Silva", "ana@example.com");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Outra Ana","email":"ana@example.com"}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void criarClienteComNomeVazioRetorna400ComMapaDeErros() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"   ","email":"ana@example.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    void criarClienteComEmailInvalidoRetorna400ComMapaDeErros() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana Silva","email":"email-invalido"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.email").exists());
    }

    @Test
    void buscarClienteInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/clientes/999999"))
                .andExpect(status().isNotFound());
    }

    private void criarCliente(String nome, String email) throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"%s","email":"%s"}
                                """.formatted(nome, email)))
                .andExpect(status().isCreated());
    }
}

package com.nicolli.mini_erp.produto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.nicolli.mini_erp.TesteApiBase;

class ProdutoApiTest extends TesteApiBase {

    @Test
    void criarProdutoValidoRetorna201() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Caderno","preco":12.50,"quantidadeEstoque":8}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Caderno"));
    }

    @Test
    void criarProdutoComNomeVazioRetorna400ComMapaDeErros() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"   ","preco":12.50,"quantidadeEstoque":8}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    void criarProdutoComPrecoNegativoRetorna400ComMapaDeErros() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Caderno","preco":-1.00,"quantidadeEstoque":8}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.preco").exists());
    }

    @Test
    void criarProdutoComEstoqueNegativoRetorna400ComMapaDeErros() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Caderno","preco":12.50,"quantidadeEstoque":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.quantidadeEstoque").exists());
    }

    @Test
    void buscarProdutoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/produtos/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarProdutosRetornaOsProdutosCadastrados() throws Exception {
        criarProduto("Caderno", "12.50", 8);
        criarProduto("Caneta", "2.00", 20);

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Caderno"))
                .andExpect(jsonPath("$[1].nome").value("Caneta"));
    }

    private void criarProduto(String nome, String preco, int quantidadeEstoque) throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"%s","preco":%s,"quantidadeEstoque":%d}
                                """.formatted(nome, preco, quantidadeEstoque)))
                .andExpect(status().isCreated());
    }
}

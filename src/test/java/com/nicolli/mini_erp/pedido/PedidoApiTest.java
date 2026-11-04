package com.nicolli.mini_erp.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.nicolli.mini_erp.TesteApiBase;
import com.nicolli.mini_erp.cliente.Cliente;
import com.nicolli.mini_erp.produto.Produto;

class PedidoApiTest extends TesteApiBase {

    @Test
    void criarPedidoValidoRetorna201CalculaTotalEBaixaEstoque() throws Exception {
        Cliente cliente = criarCliente();
        Produto produto = criarProduto("25.50", 10);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson(cliente.getId(), produto.getId(), 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(51.0));

        assertEquals(8, produtoRepository.findById(produto.getId()).orElseThrow().getQuantidadeEstoque());
    }

    @Test
    void estoqueInsuficienteRetorna422ESemAlterarQualquerEstoque() throws Exception {
        Cliente cliente = criarCliente();
        Produto disponivel = criarProduto("10.00", 5);
        Produto insuficiente = criarProduto("20.00", 1);
        String conteudo = """
                {"clienteId":%d,"itens":[{"produtoId":%d,"quantidade":2},{"produtoId":%d,"quantidade":2}]}
                """.formatted(cliente.getId(), disponivel.getId(), insuficiente.getId());

        mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(conteudo))
                .andExpect(status().isUnprocessableEntity());

        assertEquals(5, produtoRepository.findById(disponivel.getId()).orElseThrow().getQuantidadeEstoque());
        assertEquals(1, produtoRepository.findById(insuficiente.getId()).orElseThrow().getQuantidadeEstoque());
    }

    @Test
    void clienteInexistenteRetorna404() throws Exception {
        Produto produto = criarProduto("10.00", 5);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson(999999L, produto.getId(), 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listaDeItensVaziaRetorna400() throws Exception {
        Cliente cliente = criarCliente();

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":%d,"itens":[]}
                                """.formatted(cliente.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.itens").exists());
    }

    @Test
    void cancelarPedidoDevolveEstoque() throws Exception {
        Cliente cliente = criarCliente();
        Produto produto = criarProduto("12.00", 7);
        String resposta = mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson(cliente.getId(), produto.getId(), 3)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long pedidoId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(resposta).get("id").asLong();
        assertEquals(4, produtoRepository.findById(produto.getId()).orElseThrow().getQuantidadeEstoque());

        mockMvc.perform(patch("/pedidos/{id}/cancelar", pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        assertEquals(7, produtoRepository.findById(produto.getId()).orElseThrow().getQuantidadeEstoque());
    }

    @Test
    void cancelarPedidoDuasVezesRetorna422NaSegundaVez() throws Exception {
        Cliente cliente = criarCliente();
        Produto produto = criarProduto("12.00", 7);
        String resposta = mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson(cliente.getId(), produto.getId(), 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long pedidoId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(resposta).get("id").asLong();

        mockMvc.perform(patch("/pedidos/{id}/cancelar", pedidoId)).andExpect(status().isOk());
        mockMvc.perform(patch("/pedidos/{id}/cancelar", pedidoId)).andExpect(status().isUnprocessableEntity());
    }

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente de teste");
        cliente.setEmail("cliente" + System.nanoTime() + "@example.com");
        return clienteRepository.save(cliente);
    }

    private Produto criarProduto(String preco, int estoque) {
        Produto produto = new Produto();
        produto.setNome("Produto de teste");
        produto.setPreco(new BigDecimal(preco));
        produto.setQuantidadeEstoque(estoque);
        return produtoRepository.save(produto);
    }

    private String pedidoJson(Long clienteId, Long produtoId, int quantidade) {
        return """
                {"clienteId":%d,"itens":[{"produtoId":%d,"quantidade":%d}]}
                """.formatted(clienteId, produtoId, quantidade);
    }
}

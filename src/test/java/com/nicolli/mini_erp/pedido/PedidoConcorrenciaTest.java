package com.nicolli.mini_erp.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.nicolli.mini_erp.TesteApiBase;
import com.nicolli.mini_erp.cliente.Cliente;
import com.nicolli.mini_erp.produto.Produto;

class PedidoConcorrenciaTest extends TesteApiBase {

    @Test
    void doisPedidosConcorrentesComEstoqueUnitarioPermitemApenasUmSucesso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente concorrente");
        cliente.setEmail("concorrente@example.com");
        cliente = clienteRepository.save(cliente);

        Produto produto = new Produto();
        produto.setNome("Produto único");
        produto.setPreco(new BigDecimal("15.00"));
        produto.setQuantidadeEstoque(1);
        produto = produtoRepository.save(produto);

        String conteudo = """
                {"clienteId":%d,"itens":[{"produtoId":%d,"quantidade":1}]}
                """.formatted(cliente.getId(), produto.getId());
        CountDownLatch largada = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<Integer> primeira = executor.submit(() -> criarPedidoAposLargada(largada, conteudo));
            Future<Integer> segunda = executor.submit(() -> criarPedidoAposLargada(largada, conteudo));
            largada.countDown();

            int statusPrimeira = primeira.get();
            int statusSegunda = segunda.get();

            assertTrue((statusPrimeira == 201 && statusSegunda == 422)
                    || (statusPrimeira == 422 && statusSegunda == 201),
                    "Esperava um pedido criado e outro recusado por estoque insuficiente");
            assertEquals(0, produtoRepository.findById(produto.getId()).orElseThrow().getQuantidadeEstoque());
        }
    }

    private int criarPedidoAposLargada(CountDownLatch largada, String conteudo) throws Exception {
        largada.await();
        MvcResult resultado = mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conteudo))
                .andReturn();
        return resultado.getResponse().getStatus();
    }
}

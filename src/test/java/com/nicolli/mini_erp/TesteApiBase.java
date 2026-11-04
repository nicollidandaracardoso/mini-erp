package com.nicolli.mini_erp;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.nicolli.mini_erp.cliente.ClienteRepository;
import com.nicolli.mini_erp.pedido.PedidoRepository;
import com.nicolli.mini_erp.produto.ProdutoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class TesteApiBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PedidoRepository pedidoRepository;

    @Autowired
    protected ClienteRepository clienteRepository;

    @Autowired
    protected ProdutoRepository produtoRepository;

    @BeforeEach
    void limparBancoAntesDoTeste() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        produtoRepository.deleteAll();
    }
}

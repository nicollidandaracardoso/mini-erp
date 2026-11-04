package com.nicolli.mini_erp.produto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.nicolli.mini_erp.common.RegraDeNegocioException;

class ProdutoTest {

    @Test
    void baixarEstoqueDiminuiQuantidadeDisponivel() {
        Produto produto = produtoComEstoque(5);

        produto.baixarEstoque(3);

        assertEquals(2, produto.getQuantidadeEstoque());
    }

    @Test
    void baixarEstoqueAcimaDoDisponivelLancaExcecaoSemAlterarEstoque() {
        Produto produto = produtoComEstoque(2);

        assertThrows(RegraDeNegocioException.class, () -> produto.baixarEstoque(3));

        assertEquals(2, produto.getQuantidadeEstoque());
    }

    @Test
    void devolverEstoqueAumentaQuantidadeDisponivel() {
        Produto produto = produtoComEstoque(2);

        produto.devolverEstoque(4);

        assertEquals(6, produto.getQuantidadeEstoque());
    }

    private Produto produtoComEstoque(int quantidade) {
        Produto produto = new Produto();
        produto.setNome("Produto de teste");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setQuantidadeEstoque(quantidade);
        return produto;
    }
}

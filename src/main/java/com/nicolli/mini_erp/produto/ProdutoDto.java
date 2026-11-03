package com.nicolli.mini_erp.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProdutoDto {

    public record Request(
            @NotBlank(message = "O nome é obrigatório") String nome,
            @NotNull(message = "O preço é obrigatório")
            @PositiveOrZero(message = "O preço deve ser maior ou igual a zero") BigDecimal preco,
            @NotNull(message = "A quantidade em estoque é obrigatória")
            @PositiveOrZero(message = "A quantidade em estoque deve ser maior ou igual a zero") Integer quantidadeEstoque) {
    }

    public record Response(Long id, String nome, BigDecimal preco, Integer quantidadeEstoque) {
    }
}

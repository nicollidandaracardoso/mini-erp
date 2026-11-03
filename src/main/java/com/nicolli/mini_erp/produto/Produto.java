package com.nicolli.mini_erp.produto;

import java.math.BigDecimal;

import com.nicolli.mini_erp.common.RegraDeNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    public void baixarEstoque(int quantidade) {
        if (quantidade > quantidadeEstoque) {
            throw new RegraDeNegocioException(
                    "Estoque insuficiente para o produto '" + nome + "': disponível "
                            + quantidadeEstoque + ", solicitado " + quantidade);
        }

        quantidadeEstoque -= quantidade;
    }

    public void devolverEstoque(int quantidade) {
        quantidadeEstoque += quantidade;
    }
}

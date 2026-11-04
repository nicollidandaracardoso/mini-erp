package com.nicolli.mini_erp.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PedidoDto {

    public record ItemRequest(
            @NotNull(message = "O ID do produto é obrigatório") Long produtoId,
            @NotNull(message = "A quantidade é obrigatória")
            @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade) {
    }

    public record Request(
            @NotNull(message = "O ID do cliente é obrigatório") Long clienteId,
            @NotEmpty(message = "O pedido deve ter pelo menos um item") List<@Valid ItemRequest> itens) {
    }

    public record ItemResponse(Long produtoId, String produto, Integer quantidade, BigDecimal precoUnitario) {
    }

    public record Response(
            Long id,
            Long clienteId,
            StatusPedido status,
            BigDecimal total,
            LocalDateTime criadoEm,
            List<ItemResponse> itens) {
    }
}

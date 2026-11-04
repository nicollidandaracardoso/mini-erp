package com.nicolli.mini_erp.pedido;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nicolli.mini_erp.cliente.Cliente;
import com.nicolli.mini_erp.cliente.ClienteRepository;
import com.nicolli.mini_erp.common.RegraDeNegocioException;
import com.nicolli.mini_erp.common.RecursoNaoEncontradoException;
import com.nicolli.mini_erp.produto.Produto;
import com.nicolli.mini_erp.produto.ProdutoRepository;

@Service
@Transactional(readOnly = true)
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoDto.Response criar(PedidoDto.Request request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado com id " + request.clienteId()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.CRIADO);

        BigDecimal total = BigDecimal.ZERO;
        List<PedidoDto.ItemRequest> itensOrdenados = request.itens().stream()
                .sorted(Comparator.comparing(PedidoDto.ItemRequest::produtoId))
                .toList();

        for (PedidoDto.ItemRequest itemRequest : itensOrdenados) {
            Produto produto = produtoRepository.buscarParaAtualizar(itemRequest.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Produto não encontrado com id " + itemRequest.produtoId()));

            produto.baixarEstoque(itemRequest.quantidade());

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemRequest.quantidade());
            item.setPrecoUnitario(produto.getPreco());
            pedido.adicionarItem(item);

            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(itemRequest.quantidade())));
        }

        pedido.setTotal(total);
        return paraResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoDto.Response cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com id " + id));

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException("O pedido " + id + " já está cancelado");
        }

        List<ItemPedido> itensOrdenados = pedido.getItens().stream()
                .sorted(Comparator.comparing(item -> item.getProduto().getId()))
                .toList();

        for (ItemPedido item : itensOrdenados) {
            Produto produto = produtoRepository.buscarParaAtualizar(item.getProduto().getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Produto não encontrado com id " + item.getProduto().getId()));
            produto.devolverEstoque(item.getQuantidade());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return paraResponse(pedido);
    }

    public List<PedidoDto.Response> listar() {
        return pedidoRepository.findAll().stream()
                .map(this::paraResponse)
                .toList();
    }

    public PedidoDto.Response buscar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com id " + id));

        return paraResponse(pedido);
    }

    private PedidoDto.Response paraResponse(Pedido pedido) {
        List<PedidoDto.ItemResponse> itens = pedido.getItens().stream()
                .map(item -> new PedidoDto.ItemResponse(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario()))
                .toList();

        return new PedidoDto.Response(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getStatus(),
                pedido.getTotal(),
                pedido.getCriadoEm(),
                itens);
    }
}

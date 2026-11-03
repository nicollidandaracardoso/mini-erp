package com.nicolli.mini_erp.produto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nicolli.mini_erp.common.RecursoNaoEncontradoException;

@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public ProdutoDto.Response criar(ProdutoDto.Request request) {
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setQuantidadeEstoque(request.quantidadeEstoque());

        return paraResponse(produtoRepository.save(produto));
    }

    public List<ProdutoDto.Response> listar() {
        return produtoRepository.findAll().stream()
                .map(this::paraResponse)
                .toList();
    }

    public ProdutoDto.Response buscar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com id " + id));

        return paraResponse(produto);
    }

    private ProdutoDto.Response paraResponse(Produto produto) {
        return new ProdutoDto.Response(
                produto.getId(), produto.getNome(), produto.getPreco(), produto.getQuantidadeEstoque());
    }
}

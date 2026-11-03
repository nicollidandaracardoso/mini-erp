package com.nicolli.mini_erp.produto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoDto.Response criar(@Valid @RequestBody ProdutoDto.Request request) {
        return produtoService.criar(request);
    }

    @GetMapping
    public List<ProdutoDto.Response> listar() {
        return produtoService.listar();
    }

    @GetMapping("/{id}")
    public ProdutoDto.Response buscar(@PathVariable Long id) {
        return produtoService.buscar(id);
    }
}

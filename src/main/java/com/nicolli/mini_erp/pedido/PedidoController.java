package com.nicolli.mini_erp.pedido;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto.Response criar(@Valid @RequestBody PedidoDto.Request request) {
        return pedidoService.criar(request);
    }

    @GetMapping
    public List<PedidoDto.Response> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/{id}")
    public PedidoDto.Response buscar(@PathVariable Long id) {
        return pedidoService.buscar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoDto.Response cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }
}

package com.nicolli.mini_erp.cliente;

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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDto.Response criar(@Valid @RequestBody ClienteDto.Request request) {
        return clienteService.criar(request);
    }

    @GetMapping
    public List<ClienteDto.Response> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteDto.Response buscar(@PathVariable Long id) {
        return clienteService.buscar(id);
    }
}

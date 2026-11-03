package com.nicolli.mini_erp.cliente;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nicolli.mini_erp.common.RegraDeNegocioException;
import com.nicolli.mini_erp.common.RecursoNaoEncontradoException;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteDto.Response criar(ClienteDto.Request request) {
        if (clienteRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("Já existe um cliente cadastrado com este e-mail");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());

        return paraResponse(clienteRepository.save(cliente));
    }

    public List<ClienteDto.Response> listar() {
        return clienteRepository.findAll().stream()
                .map(this::paraResponse)
                .toList();
    }

    public ClienteDto.Response buscar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com id " + id));

        return paraResponse(cliente);
    }

    private ClienteDto.Response paraResponse(Cliente cliente) {
        return new ClienteDto.Response(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }
}

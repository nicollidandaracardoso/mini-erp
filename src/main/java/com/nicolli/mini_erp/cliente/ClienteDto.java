package com.nicolli.mini_erp.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClienteDto {

    public record Request(
            @NotBlank(message = "O nome é obrigatório") String nome,
            @NotBlank(message = "O e-mail é obrigatório")
            @Email(message = "E-mail inválido") String email) {
    }

    public record Response(Long id, String nome, String email) {
    }
}
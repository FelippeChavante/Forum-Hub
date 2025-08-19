package br.com.alura.forumhub.backend.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Dto para receber os dados de login. */
public record LoginDto(
    @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido")
        String email,
    @NotBlank(message = "A senha é obrigatória") String senha) {}

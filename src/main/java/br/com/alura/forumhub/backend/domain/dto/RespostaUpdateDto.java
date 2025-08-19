package br.com.alura.forumhub.backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dto para atualização de respostas. */
public record RespostaUpdateDto(
    @NotNull(message = "O ID da resposta é obrigatório") Integer id,
    @NotBlank(message = "A mensagem é obrigatória")
        @Size(min = 5, message = "A mensagem deve ter no mínimo 5 caracteres")
        String mensagem,
    boolean solucao) {}

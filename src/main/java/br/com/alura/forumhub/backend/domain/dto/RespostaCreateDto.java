package br.com.alura.forumhub.backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dto para criação de respostas. */
public record RespostaCreateDto(
    @NotBlank(message = "A mensagem é obrigatória")
        @Size(min = 5, message = "A mensagem deve ter no mínimo 5 caracteres")
        String mensagem,
    @NotNull(message = "O ID do tópico é obrigatório") Integer topicoId,
    @NotNull(message = "O ID do autor é obrigatório") Integer autorId,
    Boolean solucao) {}

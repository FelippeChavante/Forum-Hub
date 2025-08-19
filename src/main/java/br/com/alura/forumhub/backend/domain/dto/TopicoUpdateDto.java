package br.com.alura.forumhub.backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dto para atualização de tópicos. */
public record TopicoUpdateDto(
    @NotNull(message = "O ID do tópico é obrigatório") Integer id,
    @NotBlank(message = "O título é obrigatório")
        @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
        String titulo,
    @NotBlank(message = "A mensagem é obrigatória")
        @Size(min = 10, message = "A mensagem deve ter no mínimo 10 caracteres")
        String mensagem) {}

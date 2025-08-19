package br.com.alura.forumhub.backend.domain.dto;

import br.com.alura.forumhub.backend.domain.model.Topico;

import java.time.LocalDateTime;

/** Dto para exibição resumida de tópicos. */
public record TopicoDto(
    Integer id,
    String titulo,
    String mensagem,
    LocalDateTime dataCriacao,
    String status,
    String autor,
    String curso) {

  /**
   * Converte um Topico para TopicoDto.
   *
   * @param topico o tópico a ser convertido
   * @return o Dto do tópico
   */
  public static TopicoDto fromEntity(Topico topico) {
    return new TopicoDto(
        topico.getId(),
        topico.getTitulo(),
        topico.getMensagem(),
        topico.getDataCriacao(),
        topico.getStatus().toString(),
        topico.getAutor().getNome(),
        topico.getCurso().getNome());
  }
}

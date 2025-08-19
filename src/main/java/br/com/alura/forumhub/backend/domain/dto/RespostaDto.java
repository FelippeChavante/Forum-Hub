package br.com.alura.forumhub.backend.domain.dto;

import br.com.alura.forumhub.backend.domain.model.Resposta;

import java.time.LocalDateTime;

/** Dto para exibição de respostas. */
public record RespostaDto(
    Integer id,
    String mensagem,
    LocalDateTime dataCriacao,
    String autor,
    Integer topicoId,
    String topicoTitulo,
    Boolean solucao) {

  /**
   * Converte uma Resposta para RespostaDto.
   *
   * @param resposta a resposta a ser convertida
   * @return o Dto da resposta
   */
  public static RespostaDto fromEntity(Resposta resposta) {
    return new RespostaDto(
        resposta.getId(),
        resposta.getMensagem(),
        resposta.getDataCriacao(),
        resposta.getAutor().getNome(),
        resposta.getTopico().getId(),
        resposta.getTopico().getTitulo(),
        resposta.getSolucao());
  }
}

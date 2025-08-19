package br.com.alura.forumhub.backend.domain.dto;

import br.com.alura.forumhub.backend.domain.model.Resposta;
import br.com.alura.forumhub.backend.domain.model.Topico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Dto para exibição detalhada de tópicos, incluindo suas respostas. */
public record TopicoDetailDto(
    Integer id,
    String titulo,
    String mensagem,
    LocalDateTime dataCriacao,
    String status,
    String autor,
    String curso,
    List<RespostaDto> respostas) {

  /**
   * Constructor with defensive copying for mutable fields.
   *
   * @param id ID do tópico
   * @param titulo título do tópico
   * @param mensagem mensagem do tópico
   * @param dataCriacao data de criação do tópico
   * @param status status do tópico
   * @param autor autor do tópico
   * @param curso curso do tópico
   * @param respostas lista de respostas do tópico
   */
  public TopicoDetailDto(
      Integer id,
      String titulo,
      String mensagem,
      LocalDateTime dataCriacao,
      String status,
      String autor,
      String curso,
      List<RespostaDto> respostas) {
    this.id = id;
    this.titulo = titulo;
    this.mensagem = mensagem;
    this.dataCriacao = dataCriacao;
    this.status = status;
    this.autor = autor;
    this.curso = curso;
    // Create defensive copy of the list
    this.respostas = respostas != null ? new ArrayList<>(respostas) : new ArrayList<>();
  }

  /**
   * Returns a defensive copy of the respostas list.
   *
   * @return a copy of the respostas list
   */
  @Override
  public List<RespostaDto> respostas() {
    return new ArrayList<>(respostas);
  }

  /**
   * Converte um Topico para TopicoDetailDto.
   *
   * @param topico o tópico a ser convertido
   * @return o Dto detalhado do tópico
   */
  public static TopicoDetailDto fromEntity(Topico topico) {
    List<RespostaDto> respostasDto =
        topico.getRespostas().stream().map(RespostaDto::fromEntity).toList();

    return new TopicoDetailDto(
        topico.getId(),
        topico.getTitulo(),
        topico.getMensagem(),
        topico.getDataCriacao(),
        topico.getStatus().toString(),
        topico.getAutor().getNome(),
        topico.getCurso().getNome(),
        respostasDto);
  }

  /** Dto para exibição de respostas dentro do tópico. */
  public record RespostaDto(
      Integer id, String mensagem, LocalDateTime dataCriacao, String autor, Boolean solucao) {

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
          resposta.getSolucao());
    }
  }
}

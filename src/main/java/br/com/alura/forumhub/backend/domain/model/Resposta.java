package br.com.alura.forumhub.backend.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/** Entidade que representa uma resposta a um tópico no fórum. */
@Entity
@Table(name = "resposta")
@Data
@NoArgsConstructor
@ToString(exclude = {"topico", "autor"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
public class Resposta {

  /**
   * Constructor with defensive copying for mutable fields.
   *
   * @param id ID da resposta
   * @param mensagem mensagem da resposta
   * @param topico tópico da resposta
   * @param dataCriacao data de criação da resposta
   * @param autor autor da resposta
   * @param solucao indica se a resposta é solução
   */
  public Resposta(
      Integer id,
      String mensagem,
      Topico topico,
      LocalDateTime dataCriacao,
      Usuario autor,
      Boolean solucao) {
    this.id = id;
    this.mensagem = mensagem;
    this.dataCriacao = dataCriacao;
    this.solucao = solucao;

    // Use setter methods for defensive copying
    this.setTopico(topico);
    this.setAutor(autor);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Integer id;

  @NotBlank(message = "A mensagem é obrigatória")
  @Size(min = 5, message = "A mensagem deve ter no mínimo 5 caracteres")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String mensagem;

  @NotNull(message = "O tópico é obrigatório")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "topico_id", nullable = false)
  private Topico topico;

  @NotNull(message = "A data de criação é obrigatória")
  @PastOrPresent(message = "A data de criação não pode ser futura")
  @Column(name = "data_criacao", nullable = false)
  private LocalDateTime dataCriacao = LocalDateTime.now();

  @NotNull(message = "O autor é obrigatório")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "autor_id", nullable = false)
  private Usuario autor;

  @NotNull(message = "O campo solução é obrigatório")
  @Column(nullable = false)
  private Boolean solucao = false;
}

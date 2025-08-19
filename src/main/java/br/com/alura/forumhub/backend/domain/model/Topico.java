package br.com.alura.forumhub.backend.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Entidade que representa um tópico no fórum. */
@Entity
@Table(
    name = "topico",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "UK_topico_titulo_mensagem",
          columnNames = {"titulo", "mensagem"})
    })
@Data
@NoArgsConstructor
@ToString(exclude = {"autor", "curso", "respostas"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
public class Topico {

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
   * @param respostas respostas do tópico
   */
  public Topico(
      Integer id,
      String titulo,
      String mensagem,
      LocalDateTime dataCriacao,
      StatusTopico status,
      Usuario autor,
      Curso curso,
      List<Resposta> respostas) {
    this.id = id;
    this.titulo = titulo;
    this.mensagem = mensagem;
    this.dataCriacao = dataCriacao;
    this.status = status;

    // Use setter methods for defensive copying
    this.setAutor(autor);
    this.setCurso(curso);
    this.setRespostas(respostas);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Integer id;

  @NotBlank(message = "O título é obrigatório")
  @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
  @Column(nullable = false)
  private String titulo;

  @NotBlank(message = "A mensagem é obrigatória")
  @Size(min = 10, message = "A mensagem deve ter no mínimo 10 caracteres")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String mensagem;

  @NotNull(message = "A data de criação é obrigatória")
  @PastOrPresent(message = "A data de criação não pode ser futura")
  @Column(name = "data_criacao", nullable = false)
  private LocalDateTime dataCriacao = LocalDateTime.now();

  @NotNull(message = "O status é obrigatório")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusTopico status = StatusTopico.NAO_RESPONDIDO;

  @NotNull(message = "O autor é obrigatório")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "autor_id", nullable = false)
  private Usuario autor;

  @NotNull(message = "O curso é obrigatório")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "curso_id", nullable = false)
  private Curso curso;

  @OneToMany(mappedBy = "topico")
  private List<Resposta> respostas = new ArrayList<>();

  /** Enum que representa os possíveis estados de um tópico. */
  public enum StatusTopico {
    NAO_RESPONDIDO,
    NAO_SOLUCIONADO,
    SOLUCIONADO,
    FECHADO
  }

}

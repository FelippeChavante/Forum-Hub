package br.com.alura.forumhub.backend.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidade que representa um curso no sistema. */
@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "O nome do curso é obrigatório")
  @Size(min = 5, max = 100, message = "O nome do curso deve ter entre 5 e 100 caracteres")
  @Column(unique = true)
  private String nome;

  @NotBlank(message = "A categoria do curso é obrigatória")
  @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres")
  private String categoria;
}

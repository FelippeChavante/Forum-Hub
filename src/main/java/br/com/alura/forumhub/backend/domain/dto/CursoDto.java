package br.com.alura.forumhub.backend.domain.dto;

import br.com.alura.forumhub.backend.domain.model.Curso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dto para exibição e manipulação de cursos. */
public record CursoDto(
    Integer id,
    @NotBlank(message = "O nome do curso é obrigatório")
        @Size(min = 5, max = 100, message = "O nome do curso deve ter entre 5 e 100 caracteres")
        String nome,
    @NotBlank(message = "A categoria do curso é obrigatória")
        @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres")
        String categoria) {

  /**
   * Converte um Curso para CursoDto.
   *
   * @param curso o curso a ser convertido
   * @return o Dto do curso
   */
  public static CursoDto fromEntity(Curso curso) {
    return new CursoDto(curso.getId(), curso.getNome(), curso.getCategoria());
  }

  /**
   * Converte um CursoDto para Curso.
   *
   * @return a entidade Curso
   */
  public Curso toEntity() {
    Curso curso = new Curso();
    curso.setId(this.id);
    curso.setNome(this.nome);
    curso.setCategoria(this.categoria);
    return curso;
  }
}

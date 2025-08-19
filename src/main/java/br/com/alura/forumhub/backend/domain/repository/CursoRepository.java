package br.com.alura.forumhub.backend.domain.repository;

import br.com.alura.forumhub.backend.domain.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repositório para operações de banco de dados relacionadas a cursos. */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

  /**
   * Busca um curso pelo nome.
   *
   * @param nome o nome do curso
   * @return o curso encontrado ou vazio
   */
  Optional<Curso> findByNome(String nome);

  /**
   * Busca cursos pela categoria.
   *
   * @param categoria a categoria dos cursos
   * @return lista de cursos da categoria especificada
   */
  List<Curso> findByCategoria(String categoria);

  /**
   * Busca cursos pelo nome contendo o texto especificado.
   *
   * @param nome parte do nome do curso
   * @return lista de cursos que contêm o texto no nome
   */
  List<Curso> findByNomeContaining(String nome);
}

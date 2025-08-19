package br.com.alura.forumhub.backend.domain.repository;

import br.com.alura.forumhub.backend.domain.model.Curso;
import br.com.alura.forumhub.backend.domain.model.Topico;
import br.com.alura.forumhub.backend.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/** Repositório para operações de banco de dados relacionadas a tópicos. */
@Repository
public interface TopicoRepository extends JpaRepository<Topico, Integer> {

  /**
   * Busca tópicos pelo curso.
   *
   * @param curso o curso dos tópicos
   * @return lista de tópicos do curso especificado
   */
  List<Topico> findByCurso(Curso curso);

  /**
   * Busca tópicos pelo curso com paginação.
   *
   * @param curso o curso dos tópicos
   * @param paginacao informações de paginação
   * @return página de tópicos do curso especificado
   */
  Page<Topico> findByCurso(Curso curso, Pageable paginacao);

  /**
   * Busca tópicos pelo nome do curso.
   *
   * @param nomeCurso o nome do curso
   * @return lista de tópicos do curso com o nome especificado
   */
  List<Topico> findByCursoNome(String nomeCurso);

  /**
   * Busca tópicos pelo autor.
   *
   * @param autor o autor dos tópicos
   * @return lista de tópicos do autor especificado
   */
  List<Topico> findByAutor(Usuario autor);

  /**
   * Busca tópicos pelo título contendo o texto especificado.
   *
   * @param titulo parte do título do tópico
   * @return lista de tópicos que contêm o texto no título
   */
  List<Topico> findByTituloContaining(String titulo);

  /**
   * Busca tópicos pelo status.
   *
   * @param status o status dos tópicos
   * @return lista de tópicos com o status especificado
   */
  List<Topico> findByStatus(Topico.StatusTopico status);

  /**
   * Busca tópicos criados após a data especificada.
   *
   * @param data a data de referência
   * @return lista de tópicos criados após a data
   */
  List<Topico> findByDataCriacaoAfter(LocalDateTime data);

  /**
   * Verifica se existe um tópico com o mesmo título e mensagem.
   *
   * @param titulo o título do tópico
   * @param mensagem a mensagem do tópico
   * @return true se existir, false caso contrário
   */
  boolean existsByTituloAndMensagem(String titulo, String mensagem);
}

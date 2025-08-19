package br.com.alura.forumhub.backend.domain.repository;

import br.com.alura.forumhub.backend.domain.model.Resposta;
import br.com.alura.forumhub.backend.domain.model.Topico;
import br.com.alura.forumhub.backend.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repositório para operações de banco de dados relacionadas a respostas. */
@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Integer> {

  /**
   * Busca respostas pelo tópico.
   *
   * @param topico o tópico das respostas
   * @return lista de respostas do tópico especificado
   */
  List<Resposta> findByTopico(Topico topico);

  /**
   * Busca respostas pelo tópico com paginação.
   *
   * @param topico o tópico das respostas
   * @param paginacao informações de paginação
   * @return página de respostas do tópico especificado
   */
  Page<Resposta> findByTopico(Topico topico, Pageable paginacao);

  /**
   * Busca respostas pelo autor.
   *
   * @param autor o autor das respostas
   * @return lista de respostas do autor especificado
   */
  List<Resposta> findByAutor(Usuario autor);

  /**
   * Busca respostas que são soluções.
   *
   * @return lista de respostas marcadas como solução
   */
  List<Resposta> findBySolucaoTrue();

  /**
   * Conta o número de respostas para um tópico.
   *
   * @param topico o tópico
   * @return o número de respostas
   */
  long countByTopico(Topico topico);
}

package br.com.alura.forumhub.backend.domain.repository;

import br.com.alura.forumhub.backend.domain.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repositório para operações de banco de dados relacionadas a perfis de usuário. */
@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

  /**
   * Busca um perfil pelo nome.
   *
   * @param nome o nome do perfil
   * @return o perfil encontrado ou vazio
   */
  Optional<Perfil> findByNome(String nome);
}

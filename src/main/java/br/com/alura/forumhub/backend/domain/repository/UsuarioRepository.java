package br.com.alura.forumhub.backend.domain.repository;

import br.com.alura.forumhub.backend.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repositório para operações de banco de dados relacionadas a usuários. */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

  /**
   * Busca um usuário pelo email.
   *
   * @param email o email do usuário
   * @return o usuário encontrado ou vazio
   */
  Optional<Usuario> findByEmail(String email);
}

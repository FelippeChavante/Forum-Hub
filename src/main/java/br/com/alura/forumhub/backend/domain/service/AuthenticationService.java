package br.com.alura.forumhub.backend.domain.service;

import br.com.alura.forumhub.backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela autenticação de usuários. Implementa UserDetailsService para ser
 * utilizado pelo Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  /**
   * Carrega um usuário pelo username (email).
   *
   * @param username o email do usuário
   * @return os detalhes do usuário
   * @throws UsernameNotFoundException se o usuário não for encontrado
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("[DEBUG_LOG] Loading user by username (email): {}", username);
    try {
      log.debug("[DEBUG_LOG] Searching for user in database with email: {}", username);
      var userOptional = usuarioRepository.findByEmail(username);

      if (userOptional.isPresent()) {
        var user = userOptional.get();
        log.debug(
            "[DEBUG_LOG] User found: id={}, email={}, authorities={}",
            user.getId(),
            user.getEmail(),
            user.getAuthorities());
        return user;
      } else {
        log.error("[DEBUG_LOG] User not found with email: {}", username);
        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
      }
    } catch (Exception e) {
      if (e instanceof UsernameNotFoundException) {
        throw e;
      }
      log.error(
          "[DEBUG_LOG] Error loading user by username: {}, error: {}", username, e.getMessage(), e);
      throw new UsernameNotFoundException("Erro ao carregar usuário: " + e.getMessage(), e);
    }
  }
}

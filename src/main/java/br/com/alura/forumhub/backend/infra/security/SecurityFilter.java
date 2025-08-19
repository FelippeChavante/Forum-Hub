package br.com.alura.forumhub.backend.infra.security;

import br.com.alura.forumhub.backend.domain.model.Usuario;
import br.com.alura.forumhub.backend.infra.security.exception.TokenValidationException;
import br.com.alura.forumhub.backend.domain.repository.UsuarioRepository;
import br.com.alura.forumhub.backend.infra.security.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Filtro de segurança para interceptar requisições e validar tokens JWT. */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UsuarioRepository usuarioRepository;

  /**
   * Método executado a cada requisição para validar o token JWT.
   *
   * @param request requisição HTTP
   * @param response resposta HTTP
   * @param filterChain cadeia de filtros
   * @throws ServletException se ocorrer um erro durante o processamento
   * @throws IOException se ocorrer um erro de I/O
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    log.debug(
        "[DEBUG_LOG] Processing request: {} {}", request.getMethod(), request.getRequestURI());

    String tokenJwt = recuperarToken(request);
    log.debug(
        "[DEBUG_LOG] JWT token retrieved from request: {}",
        tokenJwt != null ? "present (length: " + tokenJwt.length() + ")" : "null");

    if (tokenJwt != null) {
      try {
        log.debug("[DEBUG_LOG] Validating JWT token and extracting subject");
        String subject = tokenService.getSubject(tokenJwt);
        log.debug("[DEBUG_LOG] Token subject (email): {}", subject);

        log.debug("[DEBUG_LOG] Looking up user with email: {}", subject);
        var userOptional = usuarioRepository.findByEmail(subject);

        if (userOptional.isPresent()) {
          UserDetails usuario = userOptional.get();
          log.debug(
              "[DEBUG_LOG] User found: id={}, email={}, authorities={}",
              ((Usuario) usuario).getId(),
              usuario.getUsername(),
              usuario.getAuthorities());

          var authentication =
              new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
          log.debug("[DEBUG_LOG] Setting authentication in SecurityContext: {}", authentication);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          log.error("[DEBUG_LOG] User not found with email: {}", subject);
          throw new UserNotFoundException("Usuário não encontrado");
        }
      } catch (UserNotFoundException e) {
        log.error("[DEBUG_LOG] User not found with email: {}", e.getMessage(), e);
        throw e;
      } catch (TokenValidationException e) {
        log.error("[DEBUG_LOG] Token validation failed: {}", e.getMessage(), e);
        throw e;
      } catch (RuntimeException e) {
        log.error("[DEBUG_LOG] Error processing JWT token: {}", e.getMessage(), e);
        throw e;
      } catch (Exception e) {
        log.error("[DEBUG_LOG] Unexpected error processing JWT token: {}", e.getMessage(), e);
        throw new RuntimeException(e);
      }
    } else {
      log.debug(
          "[DEBUG_LOG] No JWT token found in request, "
              + "continuing filter chain without authentication");
    }

    log.debug("[DEBUG_LOG] Continuing filter chain");
    filterChain.doFilter(request, response);
  }

  /**
   * Recupera o token JWT do cabeçalho Authorization.
   *
   * @param request requisição HTTP
   * @return o token JWT ou null se não existir
   */
  private String recuperarToken(HttpServletRequest request) {
    log.debug("[DEBUG_LOG] Retrieving token from request headers");
    String authorizationHeader = request.getHeader("Authorization");
    log.debug(
        "[DEBUG_LOG] Authorization header: {}",
        authorizationHeader != null
            ? "present (starts with: "
                + (authorizationHeader.length() > 15
                    ? authorizationHeader.substring(0, 15) + "..."
                    : authorizationHeader)
                + ")"
            : "null");

    if (authorizationHeader != null) {
      if (authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.replace("Bearer ", "");
        log.debug("[DEBUG_LOG] Bearer token extracted, length: {}", token.length());
        return token;
      } else {
        log.debug("[DEBUG_LOG] Authorization header does not start with 'Bearer '");
      }
    }

    log.debug("[DEBUG_LOG] No valid token found in request");
    return null;
  }
}

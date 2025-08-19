package br.com.alura.forumhub.backend.controller;

import br.com.alura.forumhub.backend.domain.dto.LoginDto;
import br.com.alura.forumhub.backend.domain.dto.TokenDto;
import br.com.alura.forumhub.backend.domain.model.Usuario;
import br.com.alura.forumhub.backend.infra.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller responsável pela autenticação de usuários. */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  /**
   * Endpoint para autenticação de usuários.
   *
   * @param loginDto dados de login
   * @return token JWT
   */
  @PostMapping
  public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
    log.debug("[DEBUG_LOG] Login attempt for email: {}", loginDto.email());
    try {
      log.debug(
          "[DEBUG_LOG] Creating authentication token with email: {} and password length: {}",
          loginDto.email(),
          loginDto.senha() != null ? loginDto.senha().length() : 0);
      var authenticationToken =
          new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.senha());

      log.debug(
          "[DEBUG_LOG] Calling authenticationManager.authenticate with token: {}",
          authenticationToken);
      var authentication = authenticationManager.authenticate(authenticationToken);
      log.debug("[DEBUG_LOG] Authentication successful: {}", authentication.isAuthenticated());

      var usuario = (Usuario) authentication.getPrincipal();
      log.debug(
          "[DEBUG_LOG] Retrieved user: id={}, email={}, authorities={}",
          usuario.getId(),
          usuario.getEmail(),
          usuario.getAuthorities());

      log.debug("[DEBUG_LOG] Generating JWT token for user: {}", usuario.getEmail());
      var tokenJwt = tokenService.gerarToken(usuario);
      log.debug("[DEBUG_LOG] JWT token generated successfully");

      log.debug("[DEBUG_LOG] Login successful for user: {}", usuario.getEmail());
      return ResponseEntity.ok(new TokenDto(tokenJwt, "Bearer"));
    } catch (Exception e) {
      log.error(
          "[DEBUG_LOG] Authentication failed for email: {}, error: {}",
          loginDto.email(),
          e.getMessage(),
          e);
      log.error(
          "[DEBUG_LOG] Error class: {}, cause: {}",
          e.getClass().getName(),
          e.getCause() != null ? e.getCause().getMessage() : "null");
      return ResponseEntity.status(401).build();
    }
  }
}

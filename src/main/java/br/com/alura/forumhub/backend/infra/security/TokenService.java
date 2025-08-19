package br.com.alura.forumhub.backend.infra.security;

import br.com.alura.forumhub.backend.domain.model.Usuario;
import br.com.alura.forumhub.backend.infra.security.exception.TokenGenerationException;
import br.com.alura.forumhub.backend.infra.security.exception.TokenValidationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Serviço responsável pela geração e validação de tokens JWT. */
@Service
@Slf4j
public class TokenService {

  @Getter
  @Value("${api.security.token.secret}")
  private String secret;

  /**
   * Gera um token JWT para o usuário.
   *
   * @param usuario o usuário para o qual o token será gerado
   * @return o token JWT gerado
   */
  public String gerarToken(Usuario usuario) {
    log.debug(
        "[DEBUG_LOG] Generating JWT token for user: id={}, email={}",
        usuario.getId(),
        usuario.getEmail());
    try {
      log.debug("[DEBUG_LOG] JWT secret length: {}", secret != null ? secret.length() : 0);
      log.debug("[DEBUG_LOG] Creating HMAC256 algorithm with secret");
      Algorithm algorithm = Algorithm.HMAC256(secret);

      Instant expiration = dataExpiracao();
      log.debug("[DEBUG_LOG] Token expiration set to: {}", expiration);

      log.debug("[DEBUG_LOG] Building JWT token with claims");
      String token =
          JWT.create()
              .withIssuer("API Forum Hub")
              .withSubject(usuario.getUsername())
              .withExpiresAt(expiration)
              .withClaim("id", usuario.getId())
              .withClaim("nome", usuario.getNome())
              .sign(algorithm);

      log.debug("[DEBUG_LOG] JWT token generated successfully, length: {}", token.length());
      return token;
    } catch (JWTCreationException | IllegalArgumentException exception) {
      log.error("[DEBUG_LOG] Error generating JWT token: {}", exception.getMessage(), exception);
      throw new TokenGenerationException("Erro ao gerar token JWT", exception);
    }
  }

  /**
   * Valida um token JWT e retorna o subject (username).
   *
   * @param tokenJwt o token JWT a ser validado
   * @return o subject (username) do token
   */
  public String getSubject(String tokenJwt) {
    log.debug("[DEBUG_LOG] Validating JWT token and extracting subject");
    try {
      log.debug("[DEBUG_LOG] JWT secret length: {}", secret != null ? secret.length() : 0);
      log.debug("[DEBUG_LOG] Creating HMAC256 algorithm with secret");
      Algorithm algorithm = Algorithm.HMAC256(secret);

      log.debug("[DEBUG_LOG] Building JWT verifier with issuer 'API Forum Hub'");
      var verifier = JWT.require(algorithm).withIssuer("API Forum Hub").build();

      log.debug("[DEBUG_LOG] Verifying token");
      var decodedJwt = verifier.verify(tokenJwt);

      String subject = decodedJwt.getSubject();
      log.debug("[DEBUG_LOG] Token validation successful, subject (email): {}", subject);
      return subject;
    } catch (JWTVerificationException exception) {
      log.error("[DEBUG_LOG] Token validation failed: {}", exception.getMessage(), exception);
      throw new TokenValidationException("Token JWT inválido ou expirado", exception);
    }
  }

  /**
   * Calcula a data de expiração do token (2 horas a partir da geração).
   *
   * @return a data de expiração do token
   */
  private Instant dataExpiracao() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiration = now.plusHours(2);
    ZoneOffset offset = ZoneOffset.of("-03:00");
    Instant expirationInstant = expiration.toInstant(offset);

    log.debug(
        "[DEBUG_LOG] Token expiration calculation: now={}, expiration={}, offset={}, result={}",
        now,
        expiration,
        offset,
        expirationInstant);

    return expirationInstant;
  }
}

package br.com.alura.forumhub.backend.infra.security.exception;

/** Exception thrown when there is an error generating a JWT token. */
public class TokenGenerationException extends IllegalStateException {

  public TokenGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}

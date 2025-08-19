package br.com.alura.forumhub.backend.infra.security.exception;

/** Exception thrown when there is an error validating a JWT token. */
public class TokenValidationException extends SecurityException {

  public TokenValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}

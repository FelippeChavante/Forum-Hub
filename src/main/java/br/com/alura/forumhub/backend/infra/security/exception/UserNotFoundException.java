package br.com.alura.forumhub.backend.infra.security.exception;

/** Exception thrown when a user is not found during authentication. */
public class UserNotFoundException extends SecurityException {

  public UserNotFoundException(String message) {
    super(message);
  }
}

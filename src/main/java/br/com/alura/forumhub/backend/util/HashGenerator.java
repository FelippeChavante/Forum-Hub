package br.com.alura.forumhub.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Utility class to generate BCrypt hashes for testing purposes. */
public class HashGenerator {
  /**
   * Main method that generates a BCrypt hash for a sample password. Outputs the password, its hash,
   * and verifies the hash matches the password.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    String password = "123456";
    String hash = encoder.encode(password);

    System.out.println("Password: " + password);
    System.out.println("BCrypt Hash: " + hash);
    System.out.println("Matches: " + encoder.matches(password, hash));
  }
}

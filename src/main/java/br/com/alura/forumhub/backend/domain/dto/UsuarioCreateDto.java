package br.com.alura.forumhub.backend.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Dto para criação de usuários. */
public record UsuarioCreateDto(
    @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,
    @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido")
        String email,
    @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,
    @NotEmpty(message = "Pelo menos um perfil deve ser informado") List<Integer> perfilIds) {

  /**
   * Constructor with defensive copying for mutable fields.
   *
   * @param nome nome do usuário
   * @param email email do usuário
   * @param senha senha do usuário
   * @param perfilIds IDs dos perfis do usuário
   */
  public UsuarioCreateDto(String nome, String email, String senha, List<Integer> perfilIds) {
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    // Create defensive copy of the list
    this.perfilIds = perfilIds != null ? List.copyOf(perfilIds) : List.of();
  }

  /**
   * Returns a defensive copy of the perfilIds list.
   *
   * @return a copy of the perfilIds list
   */
  @Override
  public List<Integer> perfilIds() {
    return List.copyOf(perfilIds);
  }
}

package br.com.alura.forumhub.backend.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Dto para atualização de usuários. */
public record UsuarioUpdateDto(
    @NotNull(message = "O ID do usuário é obrigatório") Integer id,
    @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,
    @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido")
        String email,

    // Senha é opcional na atualização
    String senha,

    // Lista de IDs dos perfis (opcional na atualização)
    List<Integer> perfilIds) {

  /**
   * Constructor with defensive copying for mutable fields.
   *
   * @param id ID do usuário
   * @param nome nome do usuário
   * @param email email do usuário
   * @param senha senha do usuário
   * @param perfilIds IDs dos perfis do usuário
   */
  public UsuarioUpdateDto(
      Integer id, String nome, String email, String senha, List<Integer> perfilIds) {
    this.id = id;
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
    return perfilIds != null ? List.copyOf(perfilIds) : List.of();
  }
}

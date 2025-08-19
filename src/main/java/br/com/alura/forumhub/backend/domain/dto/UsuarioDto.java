package br.com.alura.forumhub.backend.domain.dto;

import br.com.alura.forumhub.backend.domain.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

/** Dto para exibição de usuários (sem informações sensíveis). */
@Schema(description = "Dados de um usuário")
public record UsuarioDto(
    @Schema(description = "ID do usuário", example = "1") Integer id,
    @Schema(description = "Nome do usuário", example = "João Silva") String nome,
    @Schema(description = "Email do usuário", example = "joao.silva@email.com") String email) {

  /**
   * Converte um Usuario para UsuarioDto.
   *
   * @param usuario o usuário a ser convertido
   * @return o Dto do usuário
   */
  public static UsuarioDto fromEntity(Usuario usuario) {
    return new UsuarioDto(usuario.getId(), usuario.getNome(), usuario.getEmail());
  }
}

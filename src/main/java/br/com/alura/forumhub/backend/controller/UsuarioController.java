package br.com.alura.forumhub.backend.controller;

import br.com.alura.forumhub.backend.domain.dto.UsuarioCreateDto;
import br.com.alura.forumhub.backend.domain.dto.UsuarioDto;
import br.com.alura.forumhub.backend.domain.dto.UsuarioUpdateDto;
import br.com.alura.forumhub.backend.domain.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/** Controller responsável pelos endpoints relacionados a usuários. */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Operações relacionadas a usuários")
@SecurityRequirement(name = "bearer-key")
public class UsuarioController {

  private final UsuarioService usuarioService;

  /**
   * Endpoint para listar todos os usuários.
   *
   * @param paginacao informações de paginação (opcional)
   * @return lista de usuários
   */
  @GetMapping
  @Operation(summary = "Listar usuários", description = "Retorna uma lista paginada de usuários")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuários listados com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
      })
  public ResponseEntity<?> listar(
      @Parameter(description = "Parâmetros de paginação") @PageableDefault(size = 10, sort = "nome")
          Pageable paginacao) {
    try {
      Page<UsuarioDto> usuarios = usuarioService.listarTodos(paginacao);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(usuarios);
    } catch (org.springframework.data.mapping.PropertyReferenceException ex) {
      Map<String, Object> body = new HashMap<>();
      body.put("timestamp", java.time.LocalDateTime.now().toString());
      body.put("status", HttpStatus.BAD_REQUEST.value());
      body.put("error", "Bad Request");
      body.put("message", "Invalid sort parameter: " + ex.getMessage());

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
  }

  /**
   * Endpoint para buscar um usuário pelo ID.
   *
   * @param id ID do usuário
   * @return detalhes do usuário
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Detalhar usuário",
      description = "Retorna os detalhes de um usuário específico")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
      })
  public ResponseEntity<UsuarioDto> detalhar(
      @Parameter(description = "ID do usuário", example = "1") @PathVariable Integer id) {
    try {
      UsuarioDto usuario = usuarioService.buscarPorId(id);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(usuario);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para criar um novo usuário.
   *
   * @param dto dados do usuário a ser criado
   * @param uriBuilder construtor de URI
   * @return usuário criado
   */
  @PostMapping
  @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário criado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou usuário já existe",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Perfil não encontrado",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
      })
  public ResponseEntity<UsuarioDto> cadastrar(
      @Parameter(description = "Dados do usuário", required = true) @RequestBody @Valid
          UsuarioCreateDto dto,
      UriComponentsBuilder uriBuilder) {
    try {
      UsuarioDto usuario = usuarioService.criar(dto);
      URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.id()).toUri();
      return ResponseEntity.created(uri).contentType(MediaType.APPLICATION_JSON).body(usuario);
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para atualizar um usuário existente.
   *
   * @param id ID do usuário
   * @param dto dados do usuário a ser atualizado
   * @return usuário atualizado
   */
  @PutMapping("/{id}")
  @Operation(
      summary = "Atualizar usuário",
      description = "Atualiza os dados de um usuário existente")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário atualizado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou ID inconsistente",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
      })
  public ResponseEntity<UsuarioDto> atualizar(
      @Parameter(description = "ID do usuário", example = "1") @PathVariable Integer id,
      @Parameter(description = "Dados atualizados do usuário", required = true) @RequestBody @Valid
          UsuarioUpdateDto dto) {
    try {
      if (!id.equals(dto.id())) {
        return ResponseEntity.badRequest().build();
      }

      UsuarioDto usuario = usuarioService.atualizar(dto);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(usuario);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Endpoint para excluir um usuário.
   *
   * @param id ID do usuário
   * @return resposta sem conteúdo
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Usuário excluído com sucesso",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Usuário não pode ser excluído pois está sendo usado",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
      })
  public ResponseEntity<Void> excluir(
      @Parameter(description = "ID do usuário", example = "1") @PathVariable Integer id) {
    try {
      usuarioService.excluir(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}

package br.com.alura.forumhub.backend.domain.service;

import br.com.alura.forumhub.backend.domain.dto.UsuarioCreateDto;
import br.com.alura.forumhub.backend.domain.dto.UsuarioDto;
import br.com.alura.forumhub.backend.domain.dto.UsuarioUpdateDto;
import br.com.alura.forumhub.backend.domain.model.Perfil;
import br.com.alura.forumhub.backend.domain.model.Usuario;
import br.com.alura.forumhub.backend.domain.repository.PerfilRepository;
import br.com.alura.forumhub.backend.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/** Serviço responsável pela lógica de negócio relacionada a usuários. */
@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PerfilRepository perfilRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Lista todos os usuários.
   *
   * @return lista de usuários
   */
  public List<UsuarioDto> listarTodos() {
    return usuarioRepository.findAll().stream().map(UsuarioDto::fromEntity).toList();
  }

  /**
   * Lista usuários com paginação.
   *
   * @param paginacao informações de paginação
   * @return página de usuários
   */
  public Page<UsuarioDto> listarTodos(Pageable paginacao) {
    return usuarioRepository.findAll(paginacao).map(UsuarioDto::fromEntity);
  }

  /**
   * Busca um usuário pelo ID.
   *
   * @param id o ID do usuário
   * @return o usuário encontrado
   * @throws EntityNotFoundException se o usuário não for encontrado
   */
  public UsuarioDto buscarPorId(Integer id) {
    Usuario usuario =
        usuarioRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));

    return UsuarioDto.fromEntity(usuario);
  }

  /**
   * Cria um novo usuário.
   *
   * @param dto dados do usuário a ser criado
   * @return o usuário criado
   * @throws DataIntegrityViolationException se já existir um usuário com o mesmo email
   * @throws EntityNotFoundException se algum perfil não for encontrado
   */
  @Transactional
  public UsuarioDto criar(UsuarioCreateDto dto) {
    // Verifica se já existe um usuário com o mesmo email
    if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
      throw new DataIntegrityViolationException("Já existe um usuário com o email: " + dto.email());
    }

    // Verifica se os perfis existem antes de prosseguir usando streams
    Set<Perfil> perfis =
        dto.perfilIds().stream()
            .map(
                perfilId ->
                    perfilRepository
                        .findById(perfilId)
                        .orElseThrow(
                            () ->
                                new EntityNotFoundException(
                                    "Perfil não encontrado com o ID: " + perfilId)))
            .collect(java.util.stream.Collectors.toSet());

    Usuario usuario = new Usuario();
    usuario.setNome(dto.nome());
    usuario.setEmail(dto.email());
    usuario.setSenha(passwordEncoder.encode(dto.senha()));
    usuario.setPerfis(perfis);

    usuarioRepository.save(usuario);

    return UsuarioDto.fromEntity(usuario);
  }

  /**
   * Atualiza um usuário existente.
   *
   * @param dto dados do usuário a ser atualizado
   * @return o usuário atualizado
   * @throws EntityNotFoundException se o usuário não for encontrado
   * @throws DataIntegrityViolationException se já existir outro usuário com o mesmo email
   */
  @Transactional
  public UsuarioDto atualizar(UsuarioUpdateDto dto) {
    Usuario usuario =
        usuarioRepository
            .findById(dto.id())
            .orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado com o ID: " + dto.id()));

    // Verifica se já existe outro usuário com o mesmo email
    usuarioRepository
        .findByEmail(dto.email())
        .ifPresent(
            u -> {
              if (!u.getId().equals(dto.id())) {
                throw new DataIntegrityViolationException(
                    "Já existe outro usuário com o email: " + dto.email());
              }
            });

    usuario.setNome(dto.nome());
    usuario.setEmail(dto.email());

    // Atualiza a senha apenas se for fornecida
    if (dto.senha() != null && !dto.senha().isBlank()) {
      usuario.setSenha(passwordEncoder.encode(dto.senha()));
    }

    // Atualiza os perfis apenas se forem fornecidos usando streams
    if (dto.perfilIds() != null && !dto.perfilIds().isEmpty()) {
      Set<Perfil> perfis =
          dto.perfilIds().stream()
              .map(
                  perfilId ->
                      perfilRepository
                          .findById(perfilId)
                          .orElseThrow(
                              () ->
                                  new EntityNotFoundException(
                                      "Perfil não encontrado com o ID: " + perfilId)))
              .collect(java.util.stream.Collectors.toSet());
      usuario.setPerfis(perfis);
    }

    usuarioRepository.save(usuario);

    return UsuarioDto.fromEntity(usuario);
  }

  /**
   * Exclui um usuário pelo ID.
   *
   * @param id o ID do usuário a ser excluído
   * @throws EntityNotFoundException se o usuário não for encontrado
   * @throws DataIntegrityViolationException se o usuário estiver sendo usado em tópicos ou
   *     respostas
   */
  @Transactional
  public void excluir(Integer id) {
    if (!usuarioRepository.existsById(id)) {
      throw new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
    }

    try {
      usuarioRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityViolationException(
          "Não é possível excluir o usuário pois ele está sendo usado em tópicos ou respostas");
    }
  }
}

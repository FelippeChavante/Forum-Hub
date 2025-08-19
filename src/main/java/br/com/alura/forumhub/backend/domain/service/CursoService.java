package br.com.alura.forumhub.backend.domain.service;

import br.com.alura.forumhub.backend.domain.dto.CursoDto;
import br.com.alura.forumhub.backend.domain.model.Curso;
import br.com.alura.forumhub.backend.domain.repository.CursoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Serviço responsável pela lógica de negócio relacionada a cursos. */
@Service
@RequiredArgsConstructor
public class CursoService {

  private final CursoRepository cursoRepository;

  /**
   * Lista todos os cursos.
   *
   * @return lista de cursos
   */
  public List<CursoDto> listarTodos() {
    return cursoRepository.findAll().stream().map(CursoDto::fromEntity).toList();
  }

  /**
   * Lista cursos com paginação.
   *
   * @param paginacao informações de paginação
   * @return página de cursos
   */
  public Page<CursoDto> listarTodos(Pageable paginacao) {
    return cursoRepository.findAll(paginacao).map(CursoDto::fromEntity);
  }

  /**
   * Busca cursos por categoria.
   *
   * @param categoria a categoria dos cursos
   * @return lista de cursos da categoria
   */
  public List<CursoDto> listarPorCategoria(String categoria) {
    return cursoRepository.findByCategoria(categoria).stream().map(CursoDto::fromEntity).toList();
  }

  /**
   * Busca cursos pelo nome contendo o texto especificado.
   *
   * @param nome parte do nome do curso
   * @return lista de cursos que contêm o texto no nome
   */
  public List<CursoDto> listarPorNome(String nome) {
    return cursoRepository.findByNomeContaining(nome).stream().map(CursoDto::fromEntity).toList();
  }

  /**
   * Busca um curso pelo ID.
   *
   * @param id o ID do curso
   * @return o curso encontrado
   * @throws EntityNotFoundException se o curso não for encontrado
   */
  public CursoDto buscarPorId(Integer id) {
    Curso curso =
        cursoRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com o ID: " + id));

    return CursoDto.fromEntity(curso);
  }

  /**
   * Cria um novo curso.
   *
   * @param dto dados do curso a ser criado
   * @return o curso criado
   * @throws DataIntegrityViolationException se já existir um curso com o mesmo nome
   */
  @Transactional
  public CursoDto criar(CursoDto dto) {
    // Verifica se já existe um curso com o mesmo nome
    if (cursoRepository.findByNome(dto.nome()).isPresent()) {
      throw new DataIntegrityViolationException("Já existe um curso com o nome: " + dto.nome());
    }

    Curso curso = new Curso();
    curso.setNome(dto.nome());
    curso.setCategoria(dto.categoria());

    cursoRepository.save(curso);

    return CursoDto.fromEntity(curso);
  }

  /**
   * Atualiza um curso existente.
   *
   * @param dto dados do curso a ser atualizado
   * @return o curso atualizado
   * @throws EntityNotFoundException se o curso não for encontrado
   * @throws DataIntegrityViolationException se já existir outro curso com o mesmo nome
   */
  @Transactional
  public CursoDto atualizar(CursoDto dto) {
    Curso curso =
        cursoRepository
            .findById(dto.id())
            .orElseThrow(
                () -> new EntityNotFoundException("Curso não encontrado com o ID: " + dto.id()));

    // Verifica se já existe outro curso com o mesmo nome
    cursoRepository
        .findByNome(dto.nome())
        .ifPresent(
            c -> {
              if (!c.getId().equals(dto.id())) {
                throw new DataIntegrityViolationException(
                    "Já existe outro curso com o nome: " + dto.nome());
              }
            });

    curso.setNome(dto.nome());
    curso.setCategoria(dto.categoria());

    cursoRepository.save(curso);

    return CursoDto.fromEntity(curso);
  }

  /**
   * Exclui um curso pelo ID.
   *
   * @param id o ID do curso a ser excluído
   * @throws EntityNotFoundException se o curso não for encontrado
   * @throws DataIntegrityViolationException se o curso estiver sendo usado em tópicos
   */
  @Transactional
  public void excluir(Integer id) {
    if (!cursoRepository.existsById(id)) {
      throw new EntityNotFoundException("Curso não encontrado com o ID: " + id);
    }

    try {
      cursoRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      throw new DataIntegrityViolationException(
          "Não é possível excluir o curso pois ele está sendo usado em tópicos");
    }
  }
}

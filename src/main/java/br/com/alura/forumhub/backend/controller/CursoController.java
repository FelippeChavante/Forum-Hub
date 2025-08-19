package br.com.alura.forumhub.backend.controller;

import br.com.alura.forumhub.backend.domain.dto.CursoDto;
import br.com.alura.forumhub.backend.domain.service.CursoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Controller responsável pelos endpoints relacionados a cursos. */
@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

  private final CursoService cursoService;

  /**
   * Endpoint para listar todos os cursos.
   *
   * @param paginacao informações de paginação (opcional)
   * @return lista de cursos
   */
  @GetMapping
  public ResponseEntity<Page<CursoDto>> listar(
      @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
    Page<CursoDto> cursos = cursoService.listarTodos(paginacao);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(cursos);
  }

  /**
   * Endpoint para listar cursos por categoria.
   *
   * @param categoria categoria dos cursos
   * @return lista de cursos da categoria
   */
  @GetMapping("/categoria/{categoria}")
  public ResponseEntity<List<CursoDto>> listarPorCategoria(@PathVariable String categoria) {
    List<CursoDto> cursos = cursoService.listarPorCategoria(categoria);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(cursos);
  }

  /**
   * Endpoint para buscar cursos pelo nome.
   *
   * @param nome parte do nome do curso
   * @return lista de cursos que contêm o texto no nome
   */
  @GetMapping("/busca")
  public ResponseEntity<List<CursoDto>> buscarPorNome(@RequestParam String nome) {
    List<CursoDto> cursos = cursoService.listarPorNome(nome);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(cursos);
  }

  /**
   * Endpoint para buscar um curso pelo ID.
   *
   * @param id ID do curso
   * @return detalhes do curso
   */
  @GetMapping("/{id}")
  public ResponseEntity<CursoDto> detalhar(@PathVariable Integer id) {
    try {
      CursoDto curso = cursoService.buscarPorId(id);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(curso);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para criar um novo curso.
   *
   * @param dto dados do curso a ser criado
   * @param uriBuilder construtor de URI
   * @return curso criado
   */
  @PostMapping
  public ResponseEntity<CursoDto> cadastrar(
      @RequestBody @Valid CursoDto dto, UriComponentsBuilder uriBuilder) {
    try {
      CursoDto curso = cursoService.criar(dto);
      URI uri = uriBuilder.path("/cursos/{id}").buildAndExpand(curso.id()).toUri();
      return ResponseEntity.created(uri).contentType(MediaType.APPLICATION_JSON).body(curso);
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Endpoint para atualizar um curso existente.
   *
   * @param id ID do curso
   * @param dto dados do curso a ser atualizado
   * @return curso atualizado
   */
  @PutMapping("/{id}")
  public ResponseEntity<CursoDto> atualizar(
      @PathVariable Integer id, @RequestBody @Valid CursoDto dto) {
    try {
      if (!id.equals(dto.id())) {
        return ResponseEntity.badRequest().build();
      }

      CursoDto curso = cursoService.atualizar(dto);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(curso);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Endpoint para excluir um curso.
   *
   * @param id ID do curso
   * @return resposta sem conteúdo
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Integer id) {
    try {
      cursoService.excluir(id);
      return ResponseEntity.status(204).build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}

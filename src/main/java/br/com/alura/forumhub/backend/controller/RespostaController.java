package br.com.alura.forumhub.backend.controller;

import br.com.alura.forumhub.backend.domain.dto.RespostaCreateDto;
import br.com.alura.forumhub.backend.domain.dto.RespostaDto;
import br.com.alura.forumhub.backend.domain.dto.RespostaUpdateDto;
import br.com.alura.forumhub.backend.domain.service.RespostaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Controller responsável pelos endpoints relacionados a respostas. */
@RestController
@RequestMapping("/respostas")
@RequiredArgsConstructor
public class RespostaController {

  private final RespostaService respostaService;

  /**
   * Endpoint para listar todas as respostas.
   *
   * @param paginacao informações de paginação (opcional)
   * @return lista de respostas
   */
  @GetMapping
  public ResponseEntity<Page<RespostaDto>> listar(
      @PageableDefault(size = 10, sort = "data_criacao") Pageable paginacao) {
    Page<RespostaDto> respostas = respostaService.listarTodas(paginacao);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(respostas);
  }

  /**
   * Endpoint para listar respostas de um tópico específico.
   *
   * @param topicoId ID do tópico
   * @return lista de respostas do tópico
   */
  @GetMapping("/topico/{topicoId}")
  public ResponseEntity<List<RespostaDto>> listarPorTopico(@PathVariable Integer topicoId) {
    try {
      List<RespostaDto> respostas = respostaService.listarPorTopico(topicoId);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(respostas);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para buscar uma resposta pelo ID.
   *
   * @param id ID da resposta
   * @return detalhes da resposta
   */
  @GetMapping("/{id}")
  public ResponseEntity<RespostaDto> detalhar(@PathVariable Integer id) {
    try {
      RespostaDto resposta = respostaService.buscarPorId(id);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resposta);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para criar uma nova resposta.
   *
   * @param dto dados da resposta a ser criada
   * @param uriBuilder construtor de URI
   * @return resposta criada
   */
  @PostMapping
  public ResponseEntity<RespostaDto> cadastrar(
      @RequestBody @Valid RespostaCreateDto dto, UriComponentsBuilder uriBuilder) {
    try {
      RespostaDto resposta = respostaService.criar(dto);
      URI uri = uriBuilder.path("/respostas/{id}").buildAndExpand(resposta.id()).toUri();
      return ResponseEntity.created(uri).contentType(MediaType.APPLICATION_JSON).body(resposta);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para atualizar uma resposta existente.
   *
   * @param id ID da resposta
   * @param dto dados da resposta a ser atualizada
   * @return resposta atualizada
   */
  @PutMapping("/{id}")
  public ResponseEntity<RespostaDto> atualizar(
      @PathVariable Integer id, @RequestBody @Valid RespostaUpdateDto dto) {
    try {
      if (!id.equals(dto.id())) {
        return ResponseEntity.badRequest().build();
      }

      RespostaDto resposta = respostaService.atualizar(dto);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resposta);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint para excluir uma resposta.
   *
   * @param id ID da resposta
   * @return resposta sem conteúdo
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Integer id) {
    try {
      respostaService.excluir(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}

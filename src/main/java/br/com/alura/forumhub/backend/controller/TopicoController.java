package br.com.alura.forumhub.backend.controller;

import br.com.alura.forumhub.backend.domain.dto.TopicoCreateDto;
import br.com.alura.forumhub.backend.domain.dto.TopicoDetailDto;
import br.com.alura.forumhub.backend.domain.dto.TopicoDto;
import br.com.alura.forumhub.backend.domain.dto.TopicoUpdateDto;
import br.com.alura.forumhub.backend.domain.service.TopicoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/** Controller responsável pelos endpoints relacionados a tópicos. */
@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
@Tag(name = "Tópicos", description = "Operações relacionadas a tópicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    private final TopicoService topicoService;

    /**
     * Endpoint para listar todos os tópicos.
     *
     * @param paginacao informações de paginação (opcional)
     * @return lista de tópicos
     */
    @GetMapping
    public ResponseEntity<Page<TopicoDto>> listar(
            @PageableDefault(size = 10, sort = "data_criacao") Pageable paginacao) {
        Page<TopicoDto> topicos = topicoService.listarTodos(paginacao);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(topicos);
    }

    /**
     * Endpoint para listar tópicos por curso.
     *
     * @param nomeCurso nome do curso
     * @return lista de tópicos do curso
     */
    @GetMapping("/curso")
    public ResponseEntity<List<TopicoDto>> listarPorCurso(@RequestParam String nomeCurso) {
        List<TopicoDto> topicos = topicoService.listarPorCurso(nomeCurso);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(topicos);
    }

    /**
     * Endpoint para buscar um tópico pelo ID.
     *
     * @param id ID do tópico
     * @return detalhes do tópico
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicoDetailDto> detalhar(@PathVariable Integer id) {
        try {
            TopicoDetailDto topico = topicoService.buscarPorId(id);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(topico);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para criar um novo tópico.
     *
     * @param dto dados do tópico a ser criado
     * @param uriBuilder construtor de URI
     * @return tópico criado
     */
    @PostMapping
    public ResponseEntity<TopicoDto> cadastrar(
            @RequestBody @Valid TopicoCreateDto dto, UriComponentsBuilder uriBuilder) {
        try {
            TopicoDto topico = topicoService.criar(dto);
            URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.id()).toUri();
            return ResponseEntity.created(uri).contentType(MediaType.APPLICATION_JSON).body(topico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para atualizar um tópico existente.
     *
     * @param id ID do tópico
     * @param dto dados do tópico a ser atualizado
     * @return tópico atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<TopicoDto> atualizar(
            @PathVariable Integer id, @RequestBody @Valid TopicoUpdateDto dto) {
        try {
            if (!id.equals(dto.id())) {
                return ResponseEntity.badRequest().build();
            }

            TopicoDto topico = topicoService.atualizar(dto);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(topico);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para excluir um tópico.
     *
     * @param id ID do tópico
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            topicoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
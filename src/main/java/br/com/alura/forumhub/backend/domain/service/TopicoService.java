package br.com.alura.forumhub.backend.domain.service;

import br.com.alura.forumhub.backend.domain.dto.TopicoCreateDto;
import br.com.alura.forumhub.backend.domain.dto.TopicoDetailDto;
import br.com.alura.forumhub.backend.domain.dto.TopicoDto;
import br.com.alura.forumhub.backend.domain.dto.TopicoUpdateDto;
import br.com.alura.forumhub.backend.domain.model.Curso;
import br.com.alura.forumhub.backend.domain.model.Topico;
import br.com.alura.forumhub.backend.domain.model.Usuario;
import br.com.alura.forumhub.backend.domain.repository.CursoRepository;
import br.com.alura.forumhub.backend.domain.repository.TopicoRepository;
import br.com.alura.forumhub.backend.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Serviço responsável pela lógica de negócio relacionada a tópicos. */
@Service
@RequiredArgsConstructor
public class TopicoService {

    private final TopicoRepository topicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    /**
     * Lista todos os tópicos.
     *
     * @return lista de tópicos
     */
    public List<TopicoDto> listarTodos() {
        return topicoRepository.findAll().stream().map(TopicoDto::fromEntity).toList();
    }

    /**
     * Lista tópicos com paginação.
     *
     * @param paginacao informações de paginação
     * @return página de tópicos
     */
    public Page<TopicoDto> listarTodos(Pageable paginacao) {
        return topicoRepository.findAll(paginacao).map(TopicoDto::fromEntity);
    }

    /**
     * Busca um tópico pelo ID.
     *
     * @param id o ID do tópico
     * @return o tópico encontrado
     * @throws EntityNotFoundException se o tópico não for encontrado
     */
    public TopicoDetailDto buscarPorId(Integer id) {
        Topico topico =
                topicoRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Tópico não encontrado com o ID: " + id));

        return TopicoDetailDto.fromEntity(topico);
    }

    /**
     * Cria um novo tópico.
     *
     * @param dto dados do tópico a ser criado
     * @return o tópico criado
     * @throws EntityNotFoundException se o autor ou curso não forem encontrados
     */
    @Transactional
    public TopicoDto criar(TopicoCreateDto dto) {
        // Verifica se já existe um tópico com o mesmo título e mensagem
        if (topicoRepository.existsByTituloAndMensagem(dto.titulo(), dto.mensagem())) {
            throw new IllegalArgumentException("Já existe um tópico com o mesmo título e mensagem");
        }

        Usuario autor =
                usuarioRepository
                        .findById(dto.autorId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException("Autor não encontrado com o ID: " + dto.autorId()));

        Curso curso =
                cursoRepository
                        .findById(dto.cursoId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException("Curso não encontrado com o ID: " + dto.cursoId()));

        Topico topico = new Topico();
        topico.setTitulo(dto.titulo());
        topico.setMensagem(dto.mensagem());
        topico.setDataCriacao(LocalDateTime.now());
        topico.setStatus(Topico.StatusTopico.NAO_RESPONDIDO);
        topico.setAutor(autor);
        topico.setCurso(curso);

        topicoRepository.save(topico);

        return TopicoDto.fromEntity(topico);
    }

    /**
     * Atualiza um tópico existente.
     *
     * @param dto dados do tópico a ser atualizado
     * @return o tópico atualizado
     * @throws EntityNotFoundException se o tópico não for encontrado
     */
    @Transactional
    public TopicoDto atualizar(TopicoUpdateDto dto) {
        Topico topico =
                topicoRepository
                        .findById(dto.id())
                        .orElseThrow(
                                () -> new EntityNotFoundException("Tópico não encontrado com o ID: " + dto.id()));

        topico.setTitulo(dto.titulo());
        topico.setMensagem(dto.mensagem());

        topicoRepository.save(topico);

        return TopicoDto.fromEntity(topico);
    }

    /**
     * Exclui um tópico pelo ID.
     *
     * @param id o ID do tópico a ser excluído
     * @throws EntityNotFoundException se o tópico não for encontrado
     */
    @Transactional
    public void excluir(Integer id) {
        if (!topicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Tópico não encontrado com o ID: " + id);
        }

        topicoRepository.deleteById(id);
    }

    /**
     * Lista tópicos por nome do curso.
     *
     * @param nomeCurso o nome do curso
     * @return lista de tópicos do curso
     */
    public List<TopicoDto> listarPorCurso(String nomeCurso) {
        return topicoRepository.findByCursoNome(nomeCurso).stream().map(TopicoDto::fromEntity).toList();
    }
}
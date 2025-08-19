package br.com.alura.forumhub.backend.domain.service;

import br.com.alura.forumhub.backend.domain.dto.RespostaCreateDto;
import br.com.alura.forumhub.backend.domain.dto.RespostaDto;
import br.com.alura.forumhub.backend.domain.dto.RespostaUpdateDto;
import br.com.alura.forumhub.backend.domain.model.Resposta;
import br.com.alura.forumhub.backend.domain.model.Topico;
import br.com.alura.forumhub.backend.domain.model.Usuario;
import br.com.alura.forumhub.backend.domain.repository.RespostaRepository;
import br.com.alura.forumhub.backend.domain.repository.TopicoRepository;
import br.com.alura.forumhub.backend.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Serviço responsável pela lógica de negócio relacionada a respostas. */
@Service
@RequiredArgsConstructor
public class RespostaService {

  private final RespostaRepository respostaRepository;
  private final TopicoRepository topicoRepository;
  private final UsuarioRepository usuarioRepository;

  /**
   * Lista todas as respostas.
   *
   * @return lista de respostas
   */
  public List<RespostaDto> listarTodas() {
    return respostaRepository.findAll().stream().map(RespostaDto::fromEntity).toList();
  }

  /**
   * Lista respostas com paginação.
   *
   * @param paginacao informações de paginação
   * @return página de respostas
   */
  public Page<RespostaDto> listarTodas(Pageable paginacao) {
    return respostaRepository.findAll(paginacao).map(RespostaDto::fromEntity);
  }

  /**
   * Lista respostas de um tópico específico.
   *
   * @param topicoId ID do tópico
   * @return lista de respostas do tópico
   */
  public List<RespostaDto> listarPorTopico(Integer topicoId) {
    Topico topico =
        topicoRepository
            .findById(topicoId)
            .orElseThrow(
                () -> new EntityNotFoundException("Tópico não encontrado com o ID: " + topicoId));

    return respostaRepository.findByTopico(topico).stream().map(RespostaDto::fromEntity).toList();
  }

  /**
   * Busca uma resposta pelo ID.
   *
   * @param id o ID da resposta
   * @return a resposta encontrada
   * @throws EntityNotFoundException se a resposta não for encontrada
   */
  public RespostaDto buscarPorId(Integer id) {
    Resposta resposta =
        respostaRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Resposta não encontrada com o ID: " + id));

    return RespostaDto.fromEntity(resposta);
  }

  /**
   * Cria uma nova resposta.
   *
   * @param dto dados da resposta a ser criada
   * @return a resposta criada
   * @throws EntityNotFoundException se o tópico ou autor não forem encontrados
   */
  @Transactional
  public RespostaDto criar(RespostaCreateDto dto) {
    Topico topico =
        topicoRepository
            .findById(dto.topicoId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Tópico não encontrado com o ID: " + dto.topicoId()));

    Usuario autor =
        usuarioRepository
            .findById(dto.autorId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Autor não encontrado com o ID: " + dto.autorId()));

    Resposta resposta = new Resposta();
    resposta.setMensagem(dto.mensagem());
    resposta.setTopico(topico);
    resposta.setDataCriacao(LocalDateTime.now());
    resposta.setAutor(autor);
    resposta.setSolucao(dto.solucao() != null && dto.solucao());

    // Se a resposta for marcada como solução, atualiza o status do tópico
    if (Boolean.TRUE.equals(resposta.getSolucao())) {
      topico.setStatus(Topico.StatusTopico.SOLUCIONADO);
      topicoRepository.save(topico);
    } else if (topico.getStatus() == Topico.StatusTopico.NAO_RESPONDIDO) {
      // Se o tópico ainda não foi respondido, atualiza o status
      topico.setStatus(Topico.StatusTopico.NAO_SOLUCIONADO);
      topicoRepository.save(topico);
    }

    respostaRepository.save(resposta);

    return RespostaDto.fromEntity(resposta);
  }

  /**
   * Atualiza uma resposta existente.
   *
   * @param dto dados da resposta a ser atualizada
   * @return a resposta atualizada
   * @throws EntityNotFoundException se a resposta não for encontrada
   */
  @Transactional
  public RespostaDto atualizar(RespostaUpdateDto dto) {
    Resposta resposta =
        respostaRepository
            .findById(dto.id())
            .orElseThrow(
                () -> new EntityNotFoundException("Resposta não encontrada com o ID: " + dto.id()));

    resposta.setMensagem(dto.mensagem());

    // Atualiza o status de solução apenas se o valor for fornecido
    boolean statusAnterior = resposta.getSolucao();
    resposta.setSolucao(dto.solucao());

    // Se o status de solução mudou, atualiza o status do tópico
    if (statusAnterior != dto.solucao()) {
      Topico topico = resposta.getTopico();

      if (dto.solucao()) {
        topico.setStatus(Topico.StatusTopico.SOLUCIONADO);
      } else {
        // Verifica se há outras respostas marcadas como solução
        boolean temOutraSolucao =
            respostaRepository.findByTopico(topico).stream()
                .anyMatch(r -> !r.getId().equals(dto.id()) && Boolean.TRUE.equals(r.getSolucao()));

        if (!temOutraSolucao) {
          topico.setStatus(Topico.StatusTopico.NAO_SOLUCIONADO);
        }
      }

      topicoRepository.save(topico);
    }

    respostaRepository.save(resposta);

    return RespostaDto.fromEntity(resposta);
  }

  /**
   * Exclui uma resposta pelo ID.
   *
   * @param id o ID da resposta a ser excluída
   * @throws EntityNotFoundException se a resposta não for encontrada
   */
  @Transactional
  public void excluir(Integer id) {
    Resposta resposta =
        respostaRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Resposta não encontrada com o ID: " + id));

    // Se a resposta era uma solução, atualiza o status do tópico
    if (Boolean.TRUE.equals(resposta.getSolucao())) {
      Topico topico = resposta.getTopico();

      // Verifica se há outras respostas marcadas como solução
      boolean temOutraSolucao =
          respostaRepository.findByTopico(topico).stream()
              .anyMatch(r -> !r.getId().equals(id) && Boolean.TRUE.equals(r.getSolucao()));

      if (!temOutraSolucao) {
        // Se não houver outras soluções, verifica se há outras respostas
        long quantidadeRespostas =
            respostaRepository.countByTopico(topico) - 1; // -1 para excluir a resposta atual

        if (quantidadeRespostas > 0) {
          topico.setStatus(Topico.StatusTopico.NAO_SOLUCIONADO);
        } else {
          topico.setStatus(Topico.StatusTopico.NAO_RESPONDIDO);
        }

        topicoRepository.save(topico);
      }
    }

    respostaRepository.deleteById(id);
  }
}

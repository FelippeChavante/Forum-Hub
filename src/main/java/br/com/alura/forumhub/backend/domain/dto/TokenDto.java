package br.com.alura.forumhub.backend.domain.dto;

/** Dto para retornar o token JWT após autenticação. */
public record TokenDto(String token, String tipo) {}

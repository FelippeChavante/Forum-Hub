package br.com.alura.forumhub.backend.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuração do Spring MVC. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Configura a negociação de conteúdo para garantir que as respostas sejam sempre em JSON.
   *
   * @param configurer configurador de negociação de conteúdo
   */
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer
        .defaultContentType(MediaType.APPLICATION_JSON)
        .favorParameter(false)
        .ignoreAcceptHeader(false)
        .useRegisteredExtensionsOnly(false);
  }
}

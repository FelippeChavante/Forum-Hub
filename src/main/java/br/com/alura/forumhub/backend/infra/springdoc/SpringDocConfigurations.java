package br.com.alura.forumhub.backend.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuração do Swagger/OpenAPI para documentação da API. */
@Configuration
public class SpringDocConfigurations {

  /**
   * Configura as informações básicas da API para o Swagger.
   *
   * @return configuração personalizada do OpenAPI
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearer-key",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .info(
            new Info()
                .title("ForumHub API")
                .description(
                    "API REST para um sistema de fórum de discussão, "
                        + "desenvolvida com Spring Boot 3")
                .version("1.0.0")
                .contact(new Contact().name("Equipe ForumHub").email("contato@forumhub.com.br"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}

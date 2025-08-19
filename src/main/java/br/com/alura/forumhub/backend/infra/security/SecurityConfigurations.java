package br.com.alura.forumhub.backend.infra.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Configurações de segurança da aplicação. */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfigurations {

  private final SecurityFilter securityFilter;

  /**
   * Constructor with validation for required dependencies. No defensive copy is made because this
   * is a Spring component that is meant to be used as a singleton.
   *
   * @param securityFilter the security filter to use in the filter chain
   */
  @SuppressWarnings("EI_EXPOSE_REP2")
  public SecurityConfigurations(SecurityFilter securityFilter) {
    // In a Spring application, dependencies are guaranteed to be non-null
    // by the dependency injection framework, so we don't need to check for null
    this.securityFilter = securityFilter;
  }

  /**
   * Configura o filtro de segurança HTTP.
   *
   * @param httpSecurity configuração de segurança HTTP
   * @return a cadeia de filtros de segurança configurada
   * @throws Exception se ocorrer um erro durante a configuração
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    log.debug("[DEBUG_LOG] Configuring SecurityFilterChain");

    log.debug("[DEBUG_LOG] Disabling CSRF protection");
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    log.debug("[DEBUG_LOG] Setting session management policy to STATELESS");
    httpSecurity.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    log.debug("[DEBUG_LOG] Configuring authorization rules");
    httpSecurity.authorizeHttpRequests(
        authorize -> {
          // Autenticação
          log.debug("[DEBUG_LOG] Allowing public access to /login endpoint");
          authorize.requestMatchers(HttpMethod.POST, "/login").permitAll();

          // Documentação Swagger
          log.debug("[DEBUG_LOG] Allowing public access to Swagger documentation");
          authorize
              .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
              .permitAll();

          // Endpoints de tópicos
          log.debug("[DEBUG_LOG] Configuring topic endpoints access");
          authorize.requestMatchers(HttpMethod.GET, "/topicos").permitAll();
          authorize.requestMatchers(HttpMethod.GET, "/topicos/**").permitAll();
          authorize.requestMatchers(HttpMethod.POST, "/topicos").authenticated();
          authorize.requestMatchers(HttpMethod.PUT, "/topicos/**").authenticated();
          authorize.requestMatchers(HttpMethod.DELETE, "/topicos/**").authenticated();

          // Endpoints de respostas
          log.debug("[DEBUG_LOG] Configuring response endpoints access");
          authorize.requestMatchers(HttpMethod.GET, "/respostas").permitAll();
          authorize.requestMatchers(HttpMethod.GET, "/respostas/**").permitAll();
          authorize.requestMatchers(HttpMethod.POST, "/respostas").authenticated();
          authorize.requestMatchers(HttpMethod.PUT, "/respostas/**").authenticated();
          authorize.requestMatchers(HttpMethod.DELETE, "/respostas/**").authenticated();

          // Endpoints de cursos
          log.debug("[DEBUG_LOG] Configuring course endpoints access");
          authorize.requestMatchers(HttpMethod.GET, "/cursos").permitAll();
          authorize.requestMatchers(HttpMethod.GET, "/cursos/**").permitAll();
          authorize.requestMatchers(HttpMethod.POST, "/cursos").hasRole("ADMIN");
          authorize.requestMatchers(HttpMethod.PUT, "/cursos/**").hasRole("ADMIN");
          authorize.requestMatchers(HttpMethod.DELETE, "/cursos/**").hasRole("ADMIN");

          // Endpoints de usuários
          log.debug("[DEBUG_LOG] Configuring user endpoints access");
          authorize.requestMatchers(HttpMethod.GET, "/usuarios").permitAll();
          authorize.requestMatchers(HttpMethod.GET, "/usuarios/**").authenticated();
          authorize.requestMatchers(HttpMethod.POST, "/usuarios").hasRole("ADMIN");
          authorize.requestMatchers(HttpMethod.PUT, "/usuarios/**").authenticated();
          authorize.requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMIN");

          // Custom endpoints de usuários
          log.debug("[DEBUG_LOG] Configuring custom user endpoints access");
          authorize.requestMatchers(HttpMethod.GET, "/custom-usuarios").permitAll();

          // Demais requisições
          log.debug("[DEBUG_LOG] Requiring authentication for all other requests");
          authorize.anyRequest().authenticated();
        });

    log.debug("[DEBUG_LOG] Adding SecurityFilter before UsernamePasswordAuthenticationFilter");
    httpSecurity.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

    log.debug("[DEBUG_LOG] Building SecurityFilterChain");
    return httpSecurity.build();
  }

  /**
   * Configura o gerenciador de autenticação.
   *
   * @param configuration configuração de autenticação
   * @return o gerenciador de autenticação configurado
   * @throws Exception se ocorrer um erro durante a configuração
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    log.debug("[DEBUG_LOG] Creating AuthenticationManager from AuthenticationConfiguration");
    var authManager = configuration.getAuthenticationManager();
    log.debug("[DEBUG_LOG] AuthenticationManager created: {}", authManager.getClass().getName());
    return authManager;
  }

  /**
   * Configura o codificador de senha.
   *
   * @return o codificador de senha configurado
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    log.debug("[DEBUG_LOG] Creating BCryptPasswordEncoder for password encoding");
    var encoder = new BCryptPasswordEncoder();
    log.debug("[DEBUG_LOG] BCryptPasswordEncoder created: {}", encoder.getClass().getName());
    return encoder;
  }
}

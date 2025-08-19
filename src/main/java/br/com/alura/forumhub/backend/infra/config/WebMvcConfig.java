package br.com.alura.forumhub.backend.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for customizing Spring MVC behavior. This class configures the
 * PageableHandlerMethodArgumentResolver to handle invalid sort parameters.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();

    // Set a fallback pageable to use when sort parameters are invalid
    resolver.setFallbackPageable(PageRequest.of(0, 10, Sort.by("id")));

    // We can't directly set ignoreUnknownSortProperties here, but it's set in
    // application.properties
    log.debug(
        "[DEBUG_LOG] Note: spring.data.web.sort.ignore-unknown-sort-properties=true"
            + " is set in application.properties");

    log.debug(
        "[DEBUG_LOG] Configured PageableHandlerMethodArgumentResolver"
            + " with ignoreUnknownSortProperties=true");

    argumentResolvers.add(resolver);
  }
}

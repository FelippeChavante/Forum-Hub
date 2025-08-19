package br.com.alura.forumhub.backend.infra.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Filter to validate sort parameters before they reach the controllers. This filter runs before
 * security filters to prevent 403 errors when invalid sort parameters are provided.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SortParameterValidationFilter extends OncePerRequestFilter {

  // List of valid properties for each entity
  private static final List<String> VALID_USUARIO_SORT_PROPERTIES =
      Arrays.asList("id", "nome", "email");
  private final ObjectMapper objectMapper;

  /**
   * Constructor with defensive copying for mutable fields.
   *
   * @param objectMapper the ObjectMapper to use for JSON serialization
   */
  public SortParameterValidationFilter(ObjectMapper objectMapper) {
    // ObjectMapper is thread-safe and immutable after configuration,
    // but we'll create a copy to be safe
    this.objectMapper = objectMapper.copy();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    String queryString = request.getQueryString();

    log.debug(
        "[DEBUG_LOG] Processing request: {} {} with query: {}",
        request.getMethod(),
        path,
        queryString);

    // Only process GET requests with sort parameters
    if (request.getMethod().equals("GET") && queryString != null && queryString.contains("sort=")) {
      String sortParam = extractSortParameter(queryString);
      log.debug("[DEBUG_LOG] Sort parameter found: {}", sortParam);

      // Validate sort parameter based on the endpoint
      if (path.startsWith("/usuarios")
          && !isValidSortProperty(sortParam, VALID_USUARIO_SORT_PROPERTIES)) {
        log.debug("[DEBUG_LOG] Invalid sort parameter for Usuario: {}", sortParam);
        sendErrorResponse(response, sortParam, "Usuario");
        return;
      }
      // Add more entity validations as needed
    }

    filterChain.doFilter(request, response);
  }

  private String extractSortParameter(String queryString) {
    // Extract the sort parameter value from the query string using streams
    return Arrays.stream(queryString.split("&"))
        .filter(param -> param.startsWith("sort="))
        .map(param -> param.substring(5))
        .findFirst()
        .orElse("");
  }

  private boolean isValidSortProperty(String sortParam, List<String> validProperties) {
    // Handle multiple sort parameters (comma-separated) using streams
    return Arrays.stream(sortParam.split(","))
        // Remove any direction suffix (e.g., "name,asc" or "name,desc")
        .map(field -> field.split(",")[0])
        // Handle property.nested format
        .map(propertyName -> propertyName.split("\\.")[0])
        // Check if all properties are valid
        .allMatch(validProperties::contains);
  }

  private void sendErrorResponse(
      HttpServletResponse response, String invalidProperty, String entityName) throws IOException {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put(
        "message",
        "Invalid sort parameter: No property '"
            + invalidProperty
            + "' found for type '"
            + entityName
            + "'");

    objectMapper.writeValue(response.getWriter(), body);
  }
}

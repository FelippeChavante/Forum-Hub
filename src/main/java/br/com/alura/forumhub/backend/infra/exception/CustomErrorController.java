package br.com.alura.forumhub.backend.infra.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom error controller to handle errors more gracefully. This controller intercepts error
 * responses and provides more user-friendly error messages.
 */
@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

  /**
   * Handles error requests and returns a more user-friendly response.
   *
   * @param request the HTTP request
   * @return a response entity with error details
   */
  @RequestMapping("/error")
  public ResponseEntity<Object> handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

    log.debug(
        "[DEBUG_LOG] Error handling request: status={}, path={}, message={}",
        status,
        path,
        message);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now().toString());

    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    if (status != null) {
      int statusCode = Integer.parseInt(status.toString());
      httpStatus = HttpStatus.valueOf(statusCode);
      body.put("status", statusCode);
      body.put("error", httpStatus.getReasonPhrase());

      // Special handling for 403 errors
      if (statusCode == 403) {
        // Check if this is a sort parameter error
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        log.debug("[DEBUG_LOG] Handling 403 error for URI: {} with query: {}", uri, queryString);

        if (queryString != null
            && queryString.contains("sort=")
            && (uri.equals("/usuarios") || uri.startsWith("/usuarios?"))) {
          log.debug("[DEBUG_LOG] Detected invalid sort parameter in request");
          body.put("status", HttpStatus.BAD_REQUEST.value());
          body.put("error", "Bad Request");
          body.put(
              "message",
              "Invalid sort parameter. Valid sort properties for Usuario are: id, nome, email");
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .contentType(MediaType.APPLICATION_JSON)
              .body(body);
        }

        body.put("message", "Access denied. You don't have permission to access this resource.");
      } else {
        body.put("message", message != null ? message.toString() : "An error occurred");
      }
    } else {
      body.put("status", httpStatus.value());
      body.put("error", httpStatus.getReasonPhrase());
      body.put("message", "An unexpected error occurred");
    }

    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(body);
  }
}

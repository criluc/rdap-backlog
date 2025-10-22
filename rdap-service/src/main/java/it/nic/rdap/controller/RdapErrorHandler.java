package it.nic.rdap.controller;

import it.nic.rdap.model.RdapErrorResponse;
import it.nic.rdap.model.RdapLink;
import it.nic.rdap.service.RdapNotFoundException;
import it.nic.rdap.service.RdapRateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static it.nic.rdap.config.RdapWebConfig.RDAP_MEDIA_TYPE;

@ControllerAdvice
public class RdapErrorHandler {

    @ExceptionHandler(RdapNotFoundException.class)
    public ResponseEntity<RdapErrorResponse> handleNotFound(RdapNotFoundException ex, HttpServletRequest request) {
        RdapErrorResponse response = new RdapErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of(ex.getMessage()),
                linksFor(request)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(RDAP_MEDIA_TYPE)
                .body(response);
    }

    @ExceptionHandler(RdapRateLimitException.class)
    public ResponseEntity<RdapErrorResponse> handleRateLimit(RdapRateLimitException ex, HttpServletRequest request) {
        RdapErrorResponse response = new RdapErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too Many Requests",
                List.of(ex.getMessage()),
                linksFor(request)
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(RDAP_MEDIA_TYPE)
                .header("Retry-After", String.valueOf(ex.retryAfter().toSeconds()))
                .body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RdapErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus resolved = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        String title = resolved.getReasonPhrase();
        RdapErrorResponse response = new RdapErrorResponse(
                resolved.value(),
                title,
                List.of(ex.getReason() != null ? ex.getReason() : title),
                linksFor(request)
        );
        return ResponseEntity.status(resolved)
                .contentType(RDAP_MEDIA_TYPE)
                .body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<RdapErrorResponse> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        RdapErrorResponse response = new RdapErrorResponse(
                HttpStatus.NOT_ACCEPTABLE.value(),
                "Not Acceptable",
                List.of("Requested media type is not supported. Use application/rdap+json."),
                linksFor(request)
        );
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(RDAP_MEDIA_TYPE)
                .body(response);
    }

    private List<RdapLink> linksFor(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return List.of(new RdapLink("self", requestUri, "application/rdap+json", null));
    }
}

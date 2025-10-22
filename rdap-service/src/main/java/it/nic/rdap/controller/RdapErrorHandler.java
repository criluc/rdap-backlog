package it.nic.rdap.controller;

import it.nic.rdap.model.RdapErrorResponse;
import it.nic.rdap.model.RdapLink;
import it.nic.rdap.service.RdapNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
                .contentType(MediaType.valueOf("application/rdap+json"))
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
                .contentType(MediaType.valueOf("application/rdap+json"))
                .body(response);
    }

    private List<RdapLink> linksFor(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return List.of(new RdapLink("self", requestUri, "application/rdap+json", null));
    }
}

package it.nic.rdap.web.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.nic.rdap.config.RdapWebConfig;
import it.nic.rdap.model.HelpResponse;
import it.nic.rdap.model.RdapErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class RdapResponseHeaderAdvice implements ResponseBodyAdvice<Object> {

    private static final DateTimeFormatter HTTP_DATE =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC);

    private final ObjectMapper objectMapper;
    private final Instant helpLastModified;

    public RdapResponseHeaderAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.helpLastModified = Instant.parse("2024-10-01T00:00:00Z");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        HttpHeaders headers = response.getHeaders();
        headers.setContentType(RdapWebConfig.RDAP_MEDIA_TYPE);

        if (body == null) {
            return null;
        }

        String etag = generateEtag(body);
        String ifNoneMatch = request.getHeaders().getFirst(HttpHeaders.IF_NONE_MATCH);

        if (body instanceof HelpResponse) {
            headers.set(HttpHeaders.CACHE_CONTROL, "public, max-age=86400");
            headers.set(HttpHeaders.EXPIRES, HTTP_DATE.format(helpLastModified.plusSeconds(86400)));
            headers.set(HttpHeaders.LAST_MODIFIED, HTTP_DATE.format(helpLastModified));
            setEtag(headers, etag);
            if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
                propagateNotModified(response, etag);
                return null;
            }
            return body;
        }

        headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
        setEtag(headers, etag);
        if (body instanceof RdapErrorResponse) {
            return body;
        }

        if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            propagateNotModified(response, etag);
            return null;
        }

        return body;
    }

    private void setEtag(HttpHeaders headers, String etag) {
        headers.setETag(etag);
    }

    private String generateEtag(Object body) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            String hash = org.springframework.util.DigestUtils.md5DigestAsHex(bytes);
            return "\"" + hash + "\"";
        } catch (JsonProcessingException e) {
            return "\"00000000\"";
        }
    }

    private void propagateNotModified(ServerHttpResponse response, String etag) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            var raw = servletResponse.getServletResponse();
            raw.setStatus(HttpStatus.NOT_MODIFIED.value());
            raw.setHeader(HttpHeaders.ETAG, etag);
            HttpHeaders headers = response.getHeaders();
            String cacheControl = headers.getFirst(HttpHeaders.CACHE_CONTROL);
            if (cacheControl != null) {
                raw.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
            }
            String expires = headers.getFirst(HttpHeaders.EXPIRES);
            if (expires != null) {
                raw.setHeader(HttpHeaders.EXPIRES, expires);
            }
            String lastModified = headers.getFirst(HttpHeaders.LAST_MODIFIED);
            if (lastModified != null) {
                raw.setHeader(HttpHeaders.LAST_MODIFIED, lastModified);
            }
        } else {
            response.setStatusCode(HttpStatus.NOT_MODIFIED);
            setEtag(response.getHeaders(), etag);
        }
    }
}

package it.nic.rdap.web.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.nic.rdap.config.RdapWebConfig;
import it.nic.rdap.model.HelpResponse;
import it.nic.rdap.model.RdapErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
@Component
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
            headers.setCacheControl(CacheControl.maxAge(86400, java.util.concurrent.TimeUnit.SECONDS).cachePublic());
            headers.set(HttpHeaders.EXPIRES, HTTP_DATE.format(helpLastModified.plusSeconds(86400)));
            headers.set(HttpHeaders.LAST_MODIFIED, HTTP_DATE.format(helpLastModified));
            setEtag(headers, etag);
            if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
                response.setStatusCode(HttpStatus.NOT_MODIFIED);
                return null;
            }
            return body;
        }

        headers.setCacheControl(CacheControl.noStore());
        setEtag(headers, etag);
        if (body instanceof RdapErrorResponse) {
            return body;
        }

        if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            response.setStatusCode(HttpStatus.NOT_MODIFIED);
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
}

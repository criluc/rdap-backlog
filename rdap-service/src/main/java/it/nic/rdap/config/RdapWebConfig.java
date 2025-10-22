package it.nic.rdap.config;

import it.nic.rdap.web.RdapRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RdapWebConfig implements WebMvcConfigurer {

    public static final MediaType RDAP_MEDIA_TYPE = MediaType.valueOf("application/rdap+json");

    private final RdapRateLimitInterceptor rateLimitInterceptor;

    public RdapWebConfig(RdapRateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst()
                .ifPresent(converter -> {
                    List<MediaType> supported = new ArrayList<>(converter.getSupportedMediaTypes());
                    if (!supported.contains(RDAP_MEDIA_TYPE)) {
                        supported.add(0, RDAP_MEDIA_TYPE);
                    }
                    if (!supported.contains(MediaType.APPLICATION_JSON)) {
                        supported.add(MediaType.APPLICATION_JSON);
                    }
                    converter.setSupportedMediaTypes(supported);
                });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor);
    }
}

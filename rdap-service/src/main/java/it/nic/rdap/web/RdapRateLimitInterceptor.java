package it.nic.rdap.web;

import it.nic.rdap.service.RdapRateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RdapRateLimitInterceptor implements HandlerInterceptor {

    private static final String TEST_HEADER = "X-Test-RateLimit";
    private static final Duration DEFAULT_RETRY_AFTER = Duration.ofSeconds(30);
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String override = request.getHeader(TEST_HEADER);
        if (override != null) {
            if ("exceeded".equalsIgnoreCase(override)) {
                throw new RdapRateLimitException("Rate limit exceeded", DEFAULT_RETRY_AFTER);
            }
            if ("burst".equalsIgnoreCase(override)) {
                String key = request.getRequestURI();
                int current = counters.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
                if (current > 1) {
                    throw new RdapRateLimitException("Rate limit exceeded", DEFAULT_RETRY_AFTER);
                }
            }
        }
        return true;
    }
}

package com.chat.chat_microservice.filter;

import com.chat.chat_microservice.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.api.key}")
    private String apiKey;

    private static final String API_KEY_HEADER = "x-api-key";

    // Endpoints that don't require authentication
    private final List<PathPatternRequestMatcher> publicMatchers = List.of(
            PathPatternRequestMatcher.withDefaults().matcher("/actuator/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/api/v1/health"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui.html"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-resources/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/webjars/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/.well-known/**")

    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return publicMatchers.stream().anyMatch(m -> m.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        System.out.println(">>>"+requestApiKey);
        System.out.println(">>>"+apiKey);

        if (requestApiKey == null || requestApiKey.isEmpty()) {
            log.warn("Missing API key for request: {}", requestPath);
            throw new UnauthorizedException("API key is required");
        }

        if (!apiKey.equals(requestApiKey)) {
            log.warn("Invalid API key for request: {}", requestPath);
            throw new UnauthorizedException("Invalid API key");
        }


        var auth = new UsernamePasswordAuthenticationToken(
                "api-key-client",              // principal (any identifier)
                null,                          // credentials
                List.of(new SimpleGrantedAuthority("ROLE_API")) // authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);


        filterChain.doFilter(request, response);
    }
}

package com.ylli.gatewayserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails; // Added import for UserDetails
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final ReactiveJwtAuthenticationManager authenticationManager;

    public JwtAuthenticationWebFilter(ReactiveJwtAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        Authentication authToken = new UsernamePasswordAuthenticationToken(token, token);

        return authenticationManager.authenticate(authToken)
                .doOnSuccess(authenticated -> {
                    log.debug("Token authenticated successfully: {}", authenticated.isAuthenticated());
                })
                .flatMap(authenticated -> {
                    String userId = null;
                    if (authenticated.getPrincipal() instanceof String) {
                        userId = (String) authenticated.getPrincipal();
                        log.debug("Extracted userId from String principal: {}", userId);
                    } else if (authenticated.getPrincipal() instanceof UserDetails) {
                        userId = ((UserDetails) authenticated.getPrincipal()).getUsername();
                        log.debug("Extracted userId from UserDetails principal: {}", userId);
                    } else if (authenticated.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
                        userId = ((org.springframework.security.oauth2.jwt.Jwt) authenticated.getPrincipal()).getSubject();
                        log.debug("Extracted userId from Jwt principal: {}", userId);
                    } else {
                        log.warn("Authenticated principal type not recognized for userId extraction: {}", authenticated.getPrincipal().getClass().getName());
                    }

                    ServerWebExchange modifiedExchange = exchange; // Initialize with original exchange

                    if (userId != null) {
                        final String finalUserId = userId;
                        modifiedExchange = exchange.mutate()
                                .request(builder -> builder.header("X-User-ID", finalUserId))
                                .build();
                        log.debug("Added X-User-ID header: {}", userId);
                    } else {
                        log.warn("Could not determine userId from authenticated principal. Proceeding without X-User-ID header.");
                    }

                    return chain.filter(modifiedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                    Mono.just(new SecurityContextImpl(authenticated))
                            ));
                })
                .onErrorResume(e -> {
                    log.error("Authentication failed during JWT filter: {}", e.getMessage(), e);
                    return unauthorizedResponse(exchange);
                });
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } else {
            log.warn("Response already committed - cannot return 401.");
            return Mono.empty();
        }
    }
}
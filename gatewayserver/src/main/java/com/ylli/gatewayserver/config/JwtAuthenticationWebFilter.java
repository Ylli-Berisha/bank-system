package com.ylli.gatewayserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

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

        Mono<Void> result = authenticationManager.authenticate(authToken)
                .doOnSubscribe(s ->
                        System.out.println("Starting authentication for token: " + token))
                .map(authenticated -> {
                    if (authenticated.isAuthenticated()) {
                        System.out.println("Authenticated token: " + authenticated);
                    } else {
                        System.out.println("Authentication failed for token: " + authenticated);
                    }
                    return authenticated;
                })
                .doOnError(e -> {
                    System.out.println("Authentication error: " + e.getMessage());
                })
                .flatMap(authenticated -> {
                    System.out.println("Authenticated token in filter: " + authenticated);

                    Authentication authToUse;

                    if (authenticated instanceof UsernamePasswordAuthenticationToken
                            && authenticated.isAuthenticated()
                            && !authenticated.getAuthorities().isEmpty()) {
                        authToUse = authenticated;
                    } else {
                        authToUse = new UsernamePasswordAuthenticationToken(
                                authenticated.getPrincipal(),
                                authenticated.getCredentials(),
                                authenticated.getAuthorities() != null ? authenticated.getAuthorities() : List.of()
                        );
                        System.out.println("Reconstructed Authentication token: " + authToUse);
                    }

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                    Mono.just(new SecurityContextImpl(authToUse))
                            ));
                });

// Force the subscription (only for debugging!)
//        result.subscribe();

        return result;



//                .switchIfEmpty(Mono.defer(() -> {
//                    System.out.println("Authentication failed: Mono empty");
//                    return unauthorizedResponse(exchange);
//                }))
//                .onErrorResume(e -> {
//                    System.out.println("Authentication error: " + e.getMessage());
//                    return unauthorizedResponse(exchange);
//                })

    }


    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } else {
            System.out.println("Response already committed — cannot return 401.");
            return Mono.empty();
        }
    }

}

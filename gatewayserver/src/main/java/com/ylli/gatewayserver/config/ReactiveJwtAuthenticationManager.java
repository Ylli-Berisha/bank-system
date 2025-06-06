package com.ylli.gatewayserver.config;

import com.ylli.shared.configs.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ReactiveJwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    public ReactiveJwtAuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return Mono.error(new IllegalStateException("Unsupported authentication token"));
        }

        String token = authentication.getCredentials().toString();

        if (!jwtUtil.validateToken(token)) {
            return Mono.empty(); // token invalid
        }

        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null || username.isBlank()) {
            System.out.println("Token is valid but subject is missing!");
            return Mono.empty(); // No principal
        }

        List<String> roles = jwtUtil.getRolesFromToken(token);
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();


        System.out.println("JWT parsed: username=" + username + ", roles=" + roles);

        Authentication authenticatedToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        return Mono.just(authenticatedToken);

    }



}

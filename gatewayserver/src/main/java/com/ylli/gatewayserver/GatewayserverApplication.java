package com.ylli.gatewayserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@ComponentScan(basePackages = {
        "com.ylli.gatewayserver",
        "com.ylli.shared.configs"
    }
)
@EnableWebFluxSecurity
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class
})
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .method(HttpMethod.OPTIONS)
                        .and()
                        .path("/**")
                        .filters(f -> f
                                .setResponseHeader("Access-Control-Allow-Origin", "http://localhost:5173")
                                .setResponseHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                                .setResponseHeader("Access-Control-Allow-Headers", "*")
                                .setResponseHeader("Access-Control-Allow-Credentials", "true")
                                .setStatus(200))
                        .uri("no://op"))
                .route(p -> p
                        .path("/accounts-service/**")
                        .filters(f -> f
                                .rewritePath("/accounts-service/(?<segment>.*)", "/${segment}")
//                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("accountsServiceCircuitBreaker")
                                        .setFallbackUri("forward:/accounts-service/accounts/fallback")))
                        .uri("lb://accounts-service"))
                .route(p -> p
                        .path("/admin-service/**")
                        .filters(f -> f
                                .rewritePath("/admin-service/(?<segment>.*)", "/${segment}")
//                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("adminServiceCircuitBreaker")
                                        .setFallbackUri("forward:/admin-service/admin/fallback")))
                        .uri("lb://admin-service"))
                .route(p -> p
                        .path("/audit-service/**")
                        .filters(f -> f
                                .rewritePath("/audit-service/(?<segment>.*)", "/${segment}")
//                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("auditServiceCircuitBreaker")
                                        .setFallbackUri("forward:/audit-service/audit/fallback")))
                        .uri("lb://audit-service"))
                .route(p -> p
                        .path("/transactions-service/**")
                        .filters(f -> f
                                .rewritePath("/transactions-service/(?<segment>.*)", "/${segment}")
//                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("transactionsServiceCircuitBreaker")
                                        .setFallbackUri("forward:/transactions-service/transactions/fallback")))
                        .uri("lb://transactions-service"))
                .route(p -> p
                        .path("/users-service/**")
                        .filters(f -> f
                                .rewritePath("/users-service/(?<segment>.*)", "/${segment}")
//                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("usersServiceCircuitBreaker")
                                        .setFallbackUri("forward:/users-service/users/fallback")))
                        .uri("lb://users-service"))
                .build();
    }

    @Bean
    public CommandLineRunner logSecurityChains(ApplicationContext ctx) {
        return args -> {
            Map<String, SecurityWebFilterChain> chains = ctx.getBeansOfType(SecurityWebFilterChain.class);
            chains.forEach((name, chain) -> System.out.println("Security chain: " + name));
        };
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }


}

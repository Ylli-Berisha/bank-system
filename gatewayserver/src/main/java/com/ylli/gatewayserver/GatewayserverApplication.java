package com.ylli.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/accounts-service/**")
                        .filters(f -> f
                                .rewritePath("/accounts-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("accountsServiceCircuitBreaker")
                                        .setFallbackUri("forward:/accounts-service/accounts/fallback")))
                        .uri("lb://accounts-service"))
                .route(p -> p
                        .path("/admin-service/**")
                        .filters(f -> f
                                .rewritePath("/admin-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("adminServiceCircuitBreaker")
                                .setFallbackUri("forward:/admin-service/admin/fallback")))
                        .uri("lb://admin-service"))
                .route(p -> p
                        .path("/audit-service/**")
                        .filters(f -> f
                                .rewritePath("/audit-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("auditServiceCircuitBreaker")
                                .setFallbackUri("forward:/audit-service/audit/fallback")))
                        .uri("lb://audit-service"))
                .route(p -> p
                        .path("/transactions-service/**")
                        .filters(f -> f
                                .rewritePath("/transactions-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("transactionsServiceCircuitBreaker")
                                .setFallbackUri("forward:/transactions-service/transactions/fallback")))
                        .uri("lb://transactions-service"))
                .route(p -> p
                        .path("/users-service/**")
                        .filters(f -> f
                                .rewritePath("/users-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", "#{T(java.time.LocalDateTime).now()}")
                                .retry(retryConfig -> retryConfig.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .circuitBreaker(config -> config.setName("usersServiceCircuitBreaker")
                                .setFallbackUri("forward:/users-service/users/fallback")))
                        .uri("lb://users-service"))
                .build();
    }
}

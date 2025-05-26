package com.ylli.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
                                .addResponseHeader("Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("accountsServiceCircuitBreaker")))
                        .uri("lb://accounts-service"))
                .route(p -> p
                        .path("admin-service/**")
                        .filters(f -> f
                                .rewritePath("/admin-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("adminServiceCircuitBreaker")))
                        .uri("lb://admin-service"))
                .route(p -> p
                        .path("/audit-service/**")
                        .filters(f -> f
                                .rewritePath("/audit-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("auditServiceCircuitBreaker")))
                        .uri("lb://audit-service"))
                .route(p -> p
                        .path("/transactions-service/**")
                        .filters(f -> f
                                .rewritePath("/transactions-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("transactionsServiceCircuitBreaker")))
                        .uri("lb://transactions-service"))
                .route(p -> p
                        .path("/users-service/**")
                        .filters(f -> f
                                .rewritePath("/users-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("usersServiceCircuitBreaker")))
                        .uri("lb://users-service"))
                .build();
    }
}

package com.ylli.shared.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

public abstract class BaseFallbackController {

    @RequestMapping("/fallback")
    public Mono<ResponseEntity<String>> fallback() {
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service temporarily not available")
        );
    }

}

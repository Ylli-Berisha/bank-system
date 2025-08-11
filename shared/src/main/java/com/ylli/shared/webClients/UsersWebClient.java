package com.ylli.shared.webClients;

import com.ylli.shared.dtos.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsersWebClient {
    private final WebClient webClient;

    public UsersWebClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8120/api/users")
                .build();
    }

    public Mono<UserDto> getUserById(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/get/{id}")
                        .build(id)
                )
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}

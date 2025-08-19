package com.ylli.shared.webClients;

import com.ylli.shared.dtos.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UsersWebClient {
    private final WebClient webClient;

    public UsersWebClient(WebClient.Builder builder, @Value("${users.service.url}") String usersServiceUrl) {
        this.webClient = builder
                .baseUrl(usersServiceUrl + "/api/users")
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

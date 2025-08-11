package com.ylli.shared.webClients;

import com.ylli.shared.dtos.AccountDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountsWebClient {
    private final WebClient webClient;


    public AccountsWebClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8080/api/accounts")
                .build();
    }

    public Mono<Page<AccountDto>> getUserAccounts(String id, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/get/user-accounts")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )
                .header("X-User-Id", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<AccountDto>>() {});
    }

}

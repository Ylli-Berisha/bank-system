package com.ylli.shared.webClients;

import com.ylli.shared.dtos.AccountDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AccountsWebClient {
    private final WebClient webClient;
    private final String accountsServiceUrl;

    public AccountsWebClient(WebClient.Builder builder, @Value("${accounts.service.url}") String accountsServiceUrl) {
        this.accountsServiceUrl = accountsServiceUrl;
        this.webClient = builder
                .baseUrl(accountsServiceUrl + "/api/accounts")
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

    @PostConstruct
    public void init() {
        log.info("USERS_SERVICE_URL resolved to: {}", accountsServiceUrl);
    }


}

package com.ylli.shared.webClients;

import com.ylli.shared.dtos.AccountCompositionDto;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.PageResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

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

    public Mono<PageResponseDto<AccountDto>> getAccountByStatus(String userId, String status, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/get/by-status")
                        .queryParam("status", status)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )
                .header("X-User-Id", userId)
                .retrieve()
                .onStatus(statusPredicate -> statusPredicate.is4xxClientError() || statusPredicate.is5xxServerError(),
                        response -> {
                            log.error("Error from accounts-service for status {}: {} {}",
                                    status, response.statusCode(), response.headers().asHttpHeaders());
                            return response.createException();
                        })
                .bodyToMono(new ParameterizedTypeReference<Page<AccountDto>>() {}) // Deserialize from downstream as Spring Page
                .map(this::mapPageToPageResponseDto) // ✨ Map to your custom PageResponseDto ✨
                .defaultIfEmpty(new PageResponseDto<>(Collections.emptyList(), 0, size, 0, 0, true, true)) // Default if downstream returns empty
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error during WebClient call for status {}: {}", status, ex.getMessage());
                    return Mono.just(new PageResponseDto<>(Collections.emptyList(), 0, size, 0, 0, true, true));
                });
    }

    private <T> PageResponseDto<T> mapPageToPageResponseDto(Page<T> page) {
        if (page == null) {
            return new PageResponseDto<>(Collections.emptyList(), 0, 0, 0, 0, true, true);
        }
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

}

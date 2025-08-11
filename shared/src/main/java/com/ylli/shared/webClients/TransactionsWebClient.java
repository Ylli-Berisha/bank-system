package com.ylli.shared.webClients;


import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.enums.LoanStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TransactionsWebClient {
    private final WebClient webClient;

    public TransactionsWebClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8110/api")
                .build();
    }

    public Mono<Page<TransactionDto>> getUserTransactions(String id, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transactions/get/user-transactions")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )
                .header("X-User-Id", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<TransactionDto>>() {});
    }

    public Mono<Page<LoanDto>> getUserLoans(String id, LoanStatus status, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/loans/get/user-loans")
                        .queryParam("status", status)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )
                .header("X-User-Id", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<LoanDto>>() {});
    }
}

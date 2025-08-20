package com.ylli.gatewayserver.controllers;

import com.ylli.shared.dtos.AccountCompositionDto;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.PageResponseDto;
import com.ylli.shared.webClients.AccountsWebClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/accounts/composition")
@Tag(name = "Accounts Composition", description = "Composition of accounts microservice operations")
@RequiredArgsConstructor
public class AccountsCompositionController {
    private final AccountsWebClient accountsWebClient;

    @Operation(summary = "Get first account pages", description = "Get the first pages for each account type for the admin panel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched first pages successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, not an admin")
    })
    @GetMapping("/get/first-pages")
    public Mono<ResponseEntity<AccountCompositionDto>> getFirstPages(@RequestHeader("X-User-Id") String userId) {
        if (userId == null || userId.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        Mono<PageResponseDto<AccountDto>> activeAccountsMono = accountsWebClient.getAccountByStatus(userId, "ACTIVE", 0, 6);
        Mono<PageResponseDto<AccountDto>> pendingAccountsMono = accountsWebClient.getAccountByStatus(userId, "PENDING_APPROVAL", 0, 6);
        Mono<PageResponseDto<AccountDto>> closedAccountsMono = accountsWebClient.getAccountByStatus(userId, "CLOSED", 0, 6);
        Mono<PageResponseDto<AccountDto>> frozenAccountsMono = accountsWebClient.getAccountByStatus(userId, "FROZEN", 0, 6);

        return Mono.zip(activeAccountsMono, pendingAccountsMono, closedAccountsMono, frozenAccountsMono)
                .map(tuple -> {
                    AccountCompositionDto accountCompositionDto = new AccountCompositionDto();
                    accountCompositionDto.setActiveAccounts(tuple.getT1());
                    accountCompositionDto.setPendingAccounts(tuple.getT2());
                    accountCompositionDto.setClosedAccounts(tuple.getT3());
                    accountCompositionDto.setFrozenAccounts(tuple.getT4());
                    return ResponseEntity.ok(accountCompositionDto);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
                    return Mono.just(ResponseEntity.status(status)
                            .body(new AccountCompositionDto(null, null, null, null)));
                })
                .onErrorResume(Exception.class, ex -> {
                    return Mono.just(ResponseEntity.internalServerError()
                            .body(new AccountCompositionDto(null, null, null, null)));
                });
    }
}

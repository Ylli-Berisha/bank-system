package com.ylli.gatewayserver.controllers;

import com.ylli.shared.webClients.AccountsWebClient;
import com.ylli.shared.webClients.TransactionsWebClient;
import com.ylli.shared.webClients.UsersWebClient;
import com.ylli.shared.dtos.*;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user/composition")
@RequiredArgsConstructor
public class UserCompositionController {
    private final UsersWebClient usersWebClient;
    private final AccountsWebClient accountsWebClient;
    private final TransactionsWebClient transactionsWebClient;

    @Operation(summary = "Get user composition", description = "Get user composition endpoint for admin actions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User composition returned successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/get/user-composition")
    public Mono<ResponseEntity<UserCompositionDto>> getUserComposition(
            @RequestHeader("X-User-Id") String adminId,
            @RequestParam String userId) {

        try {
            validateAdmin(adminId);
        } catch (ResourceNotFoundException e) {
            return Mono.just(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        Mono<UserDto> userMono = usersWebClient.getUserById(userId);

        Mono<Page<AccountDto>> accountsMono = accountsWebClient.getUserAccounts(userId, 0, 10);

        Mono<Page<TransactionDto>> transactionsMono = transactionsWebClient.getUserTransactions(userId, 0, 10);

        Mono<Page<LoanDto>> loansMono = transactionsWebClient.getUserLoans(userId, null, 0, 10);

        return Mono.zip(userMono, accountsMono, transactionsMono, loansMono)
                .map(tuple -> {
                    UserCompositionDto dto = new UserCompositionDto();
                    dto.setUser(tuple.getT1());
                    dto.setAccounts(tuple.getT2());
                    dto.setTransactions(tuple.getT3());
                    dto.setLoans(tuple.getT4());
                    return ResponseEntity.ok(dto);
                });
    }


    private void validateAdmin(String adminId) {
        usersWebClient.getUserById(adminId)
                .flatMap(userDto -> {
                    if (userDto == null)
                        return Mono.error(new ResourceNotFoundException("User not found"));
                    if (!userDto.getRoles().contains(UserRole.ROLE_ADMIN))
                        return Mono.error(new RuntimeException("User does not have admin role"));
                    return Mono.empty();
                });
    }
}

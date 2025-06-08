package com.ylli.accounts_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.dtos.AccountDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Accounts",
        description = "Operations related to accounts"
)
@RestController
@RequestMapping("/api/accounts")
public class AccountsController extends BaseController<AccountDto, String, AccountsService> {

    @Autowired
    public AccountsController(AccountsService accountsService) {
        super(accountsService);
    }

    @GetMapping("/get/user-accounts")
    public List<AccountDto> getUserAccounts(@RequestParam String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return service.getUserAccounts(userId);
    }


}




//@RestController
//@RequestMapping("/api/accounts/")
//public class AccountsController {
//
//    @Autowired
//    private AccountsService accountsService;
//
//    @GetMapping("get/{id}")
//    public AccountDto getAccount(@PathVariable String id) {
//        if (id == null || id.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        return accountsService.getById(id);
//    }
//
//    @PostMapping("create")
//    public AccountDto createAccount(@Valid @RequestBody AccountDto accountDto) {
//        return accountsService.create(accountDto);
//    }
//
//    @PutMapping("update/{id}")
//    public AccountDto updateAccount(@PathVariable String id, @Valid @RequestBody AccountDto accountDto) {
//        if (id == null || id.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        return accountsService.update(id, accountDto);
//    }
//
//    @DeleteMapping("delete/{id}")
//    public AccountDto deleteAccount(@PathVariable String id) {
//        if (id == null || id.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        return accountsService.delete(id);
//    }
//}
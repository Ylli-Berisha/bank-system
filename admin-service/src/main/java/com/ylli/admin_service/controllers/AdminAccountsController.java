package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminAccountsService;
import com.ylli.shared.dtos.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AdminAccountsController {

    private final AdminAccountsService adminAccountsService;

    @Autowired
    public AdminAccountsController(AdminAccountsService adminAccountsService) {
        this.adminAccountsService = adminAccountsService;
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<AccountDto> accounts = adminAccountsService.getAllAccounts();
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while fetching accounts.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PatchMapping("/{id}/freeze")
//    public ResponseEntity<?> freezeAccount(@PathVariable String id) {
//        try {
//            boolean result = adminAccountsService.freezeAccount(id);
//            if (result) {
//                return new ResponseEntity<>("Account frozen successfully.", HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>("Failed to freeze account.", HttpStatus.BAD_REQUEST);
//            }
//        } catch (IllegalArgumentException e) {
//            System.err.println("Error freezing account: " + e.getMessage());
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            System.err.println("An unexpected error occurred while freezing the account: " + e.getMessage());
//            return new ResponseEntity<>("An internal server error occurred while freezing the account.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PatchMapping("/{id}/unfreeze")
//    public ResponseEntity<?> unfreezeAccount(@PathVariable String id) {
//        try {
//            boolean result = adminAccountsService.unfreezeAccount(id);
//            if (result) {
//                return new ResponseEntity<>("Account unfrozen successfully.", HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>("Failed to unfreeze account.", HttpStatus.BAD_REQUEST);
//            }
//        } catch (IllegalArgumentException e) {
//            System.err.println("Error unfreezing account: " + e.getMessage());
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            System.err.println("An unexpected error occurred while unfreezing the account: " + e.getMessage());
//            return new ResponseEntity<>("An internal server error occurred while unfreezing the account.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PatchMapping("/approve/account/{id}")
    public ResponseEntity<?> approveAccount(@PathVariable String id) {
        try{
            adminAccountsService.approveAccount(id);
        }catch (IllegalArgumentException e) {
            System.err.println("Error approving account: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while approving the account: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while approving the account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }

}

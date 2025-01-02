package com.comp5348.bank.controller;

import com.comp5348.bank.dto.AccountDTO;
import com.comp5348.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Interface
 */
@RestController
@RequestMapping("/api/customer/{customerId}/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @PathVariable Long customerId, @RequestBody CreateAccountRequest request) {
        AccountDTO account = accountService.createAccount(customerId, request.accountName);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(
            @PathVariable Long customerId,
            @PathVariable Long accountId) {
        AccountDTO account = accountService.getAccount(customerId, accountId);
        return ResponseEntity.ok(account);
    }

    public static class CreateAccountRequest {
        public String accountName;
    }
}

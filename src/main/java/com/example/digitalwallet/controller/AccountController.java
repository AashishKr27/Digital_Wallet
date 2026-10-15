package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.response.AccountResponse;
import com.example.digitalwallet.entity.Account;
import com.example.digitalwallet.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount() {
        Account account = accountService.createAccount();

        AccountResponse response = new AccountResponse();

        response.setId(account.getId());
        response.setUserId(account.getUserId());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("")
    public ResponseEntity<AccountResponse> getAccount() {
        Account account = accountService.getAccount();

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setUserId(account.getUserId());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}

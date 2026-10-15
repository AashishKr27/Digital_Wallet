package com.example.digitalwallet.service;

import com.example.digitalwallet.entity.Account;
import com.example.digitalwallet.exception.AccountAlreadyExistsException;
import com.example.digitalwallet.exception.AccountNotFoundException;
import com.example.digitalwallet.repository.AccountRepository;
import com.example.digitalwallet.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public AccountService(
            AccountRepository accountRepository,
            AuthenticatedUser authenticatedUser
    ) {
        this.accountRepository = accountRepository;
        this.authenticatedUser = authenticatedUser;
    }

    public Account createAccount() {
        Long authenticatedUserId = authenticatedUser.authenticatedUserId();

        if(accountRepository.existsByUserId(authenticatedUserId)) {
            throw new AccountAlreadyExistsException(authenticatedUserId);
        }

        Account account = new Account();
        account.setUserId(authenticatedUserId);
        return accountRepository.save(account);
    }

    @Transactional
    public Account getAccount() {
        Long authenticatedUserId = authenticatedUser.authenticatedUserId();

        return accountRepository
                .findAccountByUserId(authenticatedUserId)
                .orElseThrow(() -> new AccountNotFoundException(authenticatedUserId));
    }
}

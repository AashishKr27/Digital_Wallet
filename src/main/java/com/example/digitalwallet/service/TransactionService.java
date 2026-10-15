package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.request.TransactionRequest;
import com.example.digitalwallet.entity.Account;
import com.example.digitalwallet.entity.Transaction;
import com.example.digitalwallet.exception.AccountNotFoundException;
import com.example.digitalwallet.exception.InsufficientBalanceException;
import com.example.digitalwallet.exception.InvalidDateRangeException;
import com.example.digitalwallet.exception.InvalidTransactionTypeException;
import com.example.digitalwallet.repository.AccountRepository;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            AuthenticatedUser authenticatedUser
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.authenticatedUser = authenticatedUser;
    }

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {

        Long authenticatedUserId = authenticatedUser.authenticatedUserId();

        Account account = accountRepository
                .findAccountByUserId(authenticatedUserId)
                .orElse(null);

        if(account == null) {
            throw new AccountNotFoundException(authenticatedUserId);
        }

        if(request.getType().equals("DEPOSIT")) {
            account.setBalance(
                    account.getBalance().add(request.getAmount())
            );
        } else if(request.getType().equals("WITHDRAWAL")) {
            if(account.getBalance().compareTo(request.getAmount()) < 0){
                throw new InsufficientBalanceException();
            } else {
                account.setBalance(
                        account.getBalance().subtract(request.getAmount())
                );
            }
        } else {
            throw new InvalidTransactionTypeException();
        }

        Transaction transaction = new Transaction();

        transaction.setAccountId(account.getId());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Page<Transaction> getTransactionsByAccount(
            Pageable pageable,
            String type,
            LocalDateTime from,
            LocalDateTime to
    ) {

        Long authenticatedUserId = authenticatedUser.authenticatedUserId();

        Account account = accountRepository
                .findAccountByUserId(authenticatedUserId)
                .orElse(null);

        if(account == null) {
            throw new AccountNotFoundException(authenticatedUserId);
        }

        boolean hasType = type != null && !type.isBlank();
        boolean hasFrom = from != null;
        boolean hasTo = to != null;

        if (hasFrom && hasTo && from.isAfter(to)) {
            throw new InvalidDateRangeException("Date range cannot be after date");
        }

        if (hasType) {
            type = type.trim().toUpperCase();
        }

        if (hasFrom && hasTo && hasType) {
            return transactionRepository.findByAccountIdAndTypeAndCreatedAtBetween(
                    account.getId(), type, from, to, pageable
            );
        }

        if (hasFrom && hasTo) {
            return transactionRepository.findByAccountIdAndCreatedAtBetween(
                    account.getId(), from, to, pageable
            );
        }

        if (hasFrom && hasType) {
            return transactionRepository.findByAccountIdAndTypeAndCreatedAtGreaterThanEqual(
                    account.getId(), type, from, pageable
            );
        }

        if (hasFrom) {
            return transactionRepository.findByAccountIdAndCreatedAtGreaterThanEqual(
                    account.getId(), from, pageable
            );
        }

        if (hasTo && hasType) {
            return transactionRepository.findByAccountIdAndTypeAndCreatedAtLessThanEqual(
                    account.getId(), type, to, pageable
            );
        }

        if (hasTo) {
            return transactionRepository.findByAccountIdAndCreatedAtLessThanEqual(
                    account.getId(), to, pageable
            );
        }

        if (hasType) {
            return transactionRepository.findByAccountIdAndType(
                    account.getId(), type, pageable
            );
        }

        return transactionRepository.findByAccountId(account.getId(), pageable);
    }
}

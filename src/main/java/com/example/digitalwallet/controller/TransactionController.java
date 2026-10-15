package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.request.TransactionRequest;
import com.example.digitalwallet.dto.response.TransactionResponse;
import com.example.digitalwallet.entity.Transaction;
import com.example.digitalwallet.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request
            ) {
        Transaction transaction = transactionService.createTransaction(request);
        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setAccountId(transaction.getAccountId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setCreatedAt(transaction.getCreatedAt());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccount(
            @RequestParam(required = false) String type,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<TransactionResponse> responses = transactionService
                .getTransactionsByAccount(pageable, type, from ,to)
                .map(transaction -> {
                    TransactionResponse response = new TransactionResponse();
                    response.setId(transaction.getId());
                    response.setAccountId(transaction.getAccountId());
                    response.setType(transaction.getType());
                    response.setAmount(transaction.getAmount());
                    response.setCreatedAt(transaction.getCreatedAt());
                    return response;
                });

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responses);
    }
}

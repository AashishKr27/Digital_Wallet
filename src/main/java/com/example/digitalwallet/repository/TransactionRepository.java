package com.example.digitalwallet.repository;

import com.example.digitalwallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdAndType(
            Long accountId,
            String type,
            Pageable pageable
    );

    Page<Transaction> findByAccountId(
            Long accountId,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndCreatedAtBetween(
            Long accountId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndTypeAndCreatedAtBetween(
            Long accountId,
            String type,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndTypeAndCreatedAtGreaterThanEqual(
            Long accountId,
            String type,
            LocalDateTime from,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndCreatedAtGreaterThanEqual(
            Long accountId,
            LocalDateTime from,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndTypeAndCreatedAtLessThanEqual(
            Long accountId,
            String type,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndCreatedAtLessThanEqual(
            Long accountId,
            LocalDateTime to,
            Pageable pageable
    );
}

package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.request.TransferRequest;
import com.example.digitalwallet.dto.response.TransferResponse;
import com.example.digitalwallet.entity.Account;
import com.example.digitalwallet.entity.Transaction;
import com.example.digitalwallet.exception.AccountNotFoundException;
import com.example.digitalwallet.exception.InsufficientBalanceException;
import com.example.digitalwallet.exception.SelfTransferNotAllowedException;
import com.example.digitalwallet.exception.UserNotFoundException;
import com.example.digitalwallet.repository.AccountRepository;
import com.example.digitalwallet.repository.IdempotencyRecordRepository;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public TransferService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            IdempotencyRecordRepository idempotencyRecordRepository,
            AuthenticatedUser authenticatedUser
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.authenticatedUser = authenticatedUser;
    }

    @Transactional
    public TransferResponse transferMoney(
            TransferRequest request,
            String idempotencyKey
    ) {
        Long senderUserId = authenticatedUser.authenticatedUserId();
        Long recipientUserId = request.getRecipientUserId();

        if(senderUserId.equals(recipientUserId)){
            throw new SelfTransferNotAllowedException();
        }

        if(!userRepository.existsById(recipientUserId)){
            throw new UserNotFoundException(recipientUserId);
        }

       Long firstUserId = Math.min(recipientUserId, senderUserId);
       Long secondUserId = Math.max(recipientUserId, senderUserId);

        Account firstAccount = accountRepository
                .findAccountByUserId(firstUserId)
                .orElse(null);

        Account secondAccount = accountRepository
                .findAccountByUserId(secondUserId)
                .orElse(null);

        if(firstAccount == null) throw new AccountNotFoundException(firstUserId);
        if(secondAccount == null) throw new AccountNotFoundException(secondUserId);

        Account senderAccount = senderUserId.equals(firstUserId)
                ? firstAccount : secondAccount;

        Account recipientAccount = recipientUserId.equals(firstUserId)
                ? firstAccount : secondAccount;

        if(senderAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(request.getAmount()));
        recipientAccount.setBalance(recipientAccount.getBalance().add(request.getAmount()));

        Transaction senderTransaction = new Transaction();
        senderTransaction.setAccountId(senderAccount.getId());
        senderTransaction.setType("WITHDRAWAL");
        senderTransaction.setAmount(request.getAmount());

        senderTransaction =  transactionRepository.save(senderTransaction);

        Transaction recipientTransaction = new Transaction();
        recipientTransaction.setAccountId(recipientAccount.getId());
        recipientTransaction.setType("DEPOSIT");
        recipientTransaction.setAmount(request.getAmount());

        recipientTransaction =  transactionRepository.save(recipientTransaction);

        return new TransferResponse(
                senderTransaction.getId(),
                recipientTransaction.getId(),
                recipientUserId,
                request.getAmount(),
                senderTransaction.getCreatedAt()
        );
    }
}

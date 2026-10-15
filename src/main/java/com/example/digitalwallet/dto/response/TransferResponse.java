package com.example.digitalwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransferResponse {

    private Long senderTransactionId;
    private Long recipientTransactionId;
    private Long recipientUserId;
    private BigDecimal amount;
    private LocalDateTime transferredAt;

}

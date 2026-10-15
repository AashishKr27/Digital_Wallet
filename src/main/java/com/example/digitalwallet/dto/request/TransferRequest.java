package com.example.digitalwallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotNull(message = "Recipient user ID is required")
    @Positive(message = "Recipient user ID must be positive")
    private Long recipientUserId;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Transfer amount must be greater than zero"
    )
    private BigDecimal amount;
}

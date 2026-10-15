package com.example.digitalwallet.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AccountResponse {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}

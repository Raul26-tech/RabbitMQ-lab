package com.raul.transferservice.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransferRequest(
        @NotNull
        UUID senderId,

        @NotNull
        UUID receiverId,

        @NotNull
        @Positive
        BigDecimal amount
) {
}
package com.raul.transferservice.application.dto;

public record IdempotencyData(
        String key,
        String requestHash
) {
}
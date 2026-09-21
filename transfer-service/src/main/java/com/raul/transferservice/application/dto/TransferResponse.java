package com.raul.transferservice.application.dto;

import com.raul.transferservice.domain.model.Transfer;
import com.raul.transferservice.domain.model.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID id,
        UUID senderId,
        UUID receiverId,
        BigDecimal amount,
        TransferStatus status,
        Instant createdAt
) {
    public static TransferResponse from(Transfer transfer){
        return new TransferResponse(
                transfer.getId(),
                transfer.getSenderId(),
                transfer.getReceiverId(),
                transfer.getAmount(),
                transfer.getStatus(),
                transfer.getCreatedAt()
        );
    }
}
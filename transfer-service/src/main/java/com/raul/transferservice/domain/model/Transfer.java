package com.raul.transferservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transfer {
    private final UUID id;
    private final UUID senderId;
    private final UUID receiverId;
    private final BigDecimal amount;
    private final TransferStatus status;
    private final Instant createdAt;

    private Transfer(
            UUID id,
            UUID senderId,
            UUID receiverId,
            BigDecimal amount,
            TransferStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Transfer create(
            UUID senderId,
            UUID receiverId,
            BigDecimal amount
    ) {

        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException(
                    "Sender and Receiver must not be null"
            );
        }

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException(
                    "Sender and Receiver must be different"
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(

                    "Amount must be greater than zero"
            );
        }

        return new Transfer(
                UUID.randomUUID(),
                senderId,
                receiverId,
                amount,
                TransferStatus.COMPLETED,
                Instant.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
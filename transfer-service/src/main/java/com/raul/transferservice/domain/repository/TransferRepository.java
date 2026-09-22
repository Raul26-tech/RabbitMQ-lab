package com.raul.transferservice.domain.repository;

import com.raul.transferservice.domain.model.Transfer;

import java.util.Optional;

public interface TransferRepository {
    Transfer save(
            Transfer transfer,
            String idempotencyKey,
            String requestHash
    );

    Optional<StoredTransfer> findByIdempotencyKey(
            String idempotencyKey
    );
}
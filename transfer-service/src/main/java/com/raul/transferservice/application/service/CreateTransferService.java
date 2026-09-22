package com.raul.transferservice.application.service;

import com.raul.transferservice.application.helper.TransferRequestHashGenerator;
import com.raul.transferservice.domain.model.Transfer;
import com.raul.transferservice.domain.repository.StoredTransfer;
import com.raul.transferservice.domain.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreateTransferService {
    private final TransferRepository transferRepository;

    public CreateTransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public Transfer execute(
            UUID senderId,
            UUID receiverId,
            BigDecimal amount,
            String idempotencyKey
    ) {
        String requestHash = TransferRequestHashGenerator.generate(
                senderId,
                receiverId,
                amount
        );


        Optional<StoredTransfer> existing =
                transferRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            StoredTransfer stored = existing.get();


            if (!stored.requesthash().equals(requestHash)) {
                throw new IllegalStateException(
                        "Idempotency-Key already used with a different request"
                );
            }

            return stored.transfer();

        }

        Transfer transfer = Transfer.create(
                senderId,
                receiverId,
                amount
        );


        return transferRepository.save(
                transfer,
                idempotencyKey,
                requestHash
        );
    }
}
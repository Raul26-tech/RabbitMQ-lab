package com.raul.transferservice.application.service;

import com.raul.transferservice.domain.model.Transfer;
import com.raul.transferservice.domain.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
            BigDecimal amount
    ){
        Transfer transfer = Transfer.create(
                senderId,
                receiverId,
                amount
        );


        return transferRepository.save(transfer);
    }
}
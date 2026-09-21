package com.raul.transferservice.application.service;

import com.raul.transferservice.domain.model.Transfer;
import com.raul.transferservice.domain.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateTransferServiceTest {

    private TransferRepository transferRepository;
    private CreateTransferService createTransferService;

    @BeforeEach
    void setUp() {
        transferRepository = mock(TransferRepository.class);
        createTransferService =
                new CreateTransferService(transferRepository);
    }

    @Test
    void shouldCreateAndSaveTransfer() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(300);

        when(transferRepository.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transfer result = createTransferService.execute(
                senderId,
                receiverId,
                amount
        );

        assertEquals(senderId, result.getSenderId());
        assertEquals(receiverId, result.getReceiverId());
        assertEquals(amount, result.getAmount());

        verify(transferRepository, times(1))
                .save(any(Transfer.class));
    }
}
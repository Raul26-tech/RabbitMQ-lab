package com.raul.transferservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    @Test
    void shouldCreateValidTransfer(){
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Transfer transfer = Transfer.create(
                senderId,
                receiverId,
                BigDecimal.valueOf(300)
        );

        assertNotNull(transfer.getId());
        assertEquals(senderId, transfer.getSenderId());
        assertEquals(receiverId, transfer.getReceiverId());
        assertEquals(BigDecimal.valueOf(300), transfer.getAmount());
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());
        assertNotNull(transfer.getCreatedAt());
    }

    @Test
    void shouldRejectTransferWhenSenderEqualsReceiver(){
        UUID sameId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> Transfer.create(
                        sameId,
                        sameId,
                        BigDecimal.valueOf(100)
                )
        );
    }

    @Test
    void shouldRejectTransferWhenAmountIsZeroOrNegative(){
        assertThrows(
                IllegalArgumentException.class,
                () -> Transfer.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        BigDecimal.valueOf(-100)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Transfer.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        BigDecimal.valueOf(0)
                )
        );
    }

    @Test
    void shouldRejectTransferWhenSenderOrReceiverIsNull(){
        assertThrows(
                IllegalArgumentException.class,
                () -> Transfer.create(
                        null,
                        UUID.randomUUID(),
                        BigDecimal.valueOf(100)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Transfer.create(
                        UUID.randomUUID(),
                        null,
                        BigDecimal.valueOf(100)
                )
        );
    }
}
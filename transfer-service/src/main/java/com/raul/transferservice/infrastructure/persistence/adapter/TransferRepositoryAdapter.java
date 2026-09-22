package com.raul.transferservice.infrastructure.persistence.adapter;

import com.raul.transferservice.domain.model.Transfer;
import com.raul.transferservice.domain.model.TransferStatus;
import com.raul.transferservice.domain.repository.StoredTransfer;
import com.raul.transferservice.domain.repository.TransferRepository;
import com.raul.transferservice.infrastructure.persistence.entity.TransferJpaEntity;
import com.raul.transferservice.infrastructure.persistence.repository.TransferJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class TransferRepositoryAdapter implements TransferRepository {

    private final TransferJpaRepository repository;

    public TransferRepositoryAdapter(TransferJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transfer save(
            Transfer transfer,
            String idempotencyKey,
            String requestHash
    ) {
        TransferJpaEntity entity = new TransferJpaEntity(
                transfer.getId(),
                transfer.getSenderId(),
                transfer.getReceiverId(),
                transfer.getAmount(),
                transfer.getStatus().name(),
                idempotencyKey,
                requestHash,
                transfer.getCreatedAt()
        );

        repository.save(entity);

        return transfer;
    }


    @Override
    public Optional<StoredTransfer> findByIdempotencyKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey)
                .map(
                        entity -> {
                            Transfer transfer = Transfer.restore(
                                    entity.getId(),
                                    entity.getSenderId(),
                                    entity.getReceiverId(),
                                    entity.getAmount(),
                                    TransferStatus.valueOf(entity.getStatus()),
                                    entity.getCreatedAt()
                            );

                            return new StoredTransfer(
                                    transfer,
                                    entity.getRequestHash()
                            );
                        }
                );
    }
}
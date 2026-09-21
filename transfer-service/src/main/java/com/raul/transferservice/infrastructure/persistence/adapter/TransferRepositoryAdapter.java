package com.raul.transferservice.infrastructure.persistence.adapter;

import com.raul.transferservice.domain.model.Transfer;
import com.raul.transferservice.domain.repository.TransferRepository;
import com.raul.transferservice.infrastructure.persistence.entity.TransferJpaEntity;
import com.raul.transferservice.infrastructure.persistence.repository.TransferJpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public class TransferRepositoryAdapter implements TransferRepository {

    private final TransferJpaRepository repository;

    public TransferRepositoryAdapter(TransferJpaRepository repository){
        this.repository = repository;
    }

    @Override
    public Transfer save(Transfer transfer){
        TransferJpaEntity entity = new TransferJpaEntity(
                transfer.getId(),
                transfer.getSenderId(),
                transfer.getReceiverId(),
                transfer.getAmount(),
                transfer.getStatus().name(),
                transfer.getCreatedAt()
        );

        repository.save(entity);

        return transfer;
    }

}
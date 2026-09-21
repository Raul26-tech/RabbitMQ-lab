package com.raul.transferservice.infrastructure.persistence.repository;

import com.raul.transferservice.infrastructure.persistence.entity.TransferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferJpaRepository
        extends JpaRepository<TransferJpaEntity, UUID> {
}
package com.raul.transferservice.domain.repository;

import com.raul.transferservice.domain.model.Transfer;

public interface TransferRepository {
    Transfer save(Transfer transfer);
}
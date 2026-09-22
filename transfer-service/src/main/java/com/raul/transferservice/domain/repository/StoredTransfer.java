package com.raul.transferservice.domain.repository;

import com.raul.transferservice.domain.model.Transfer;

public record StoredTransfer(
        Transfer transfer,
        String requesthash
) {
}
package com.raul.transferservice.presentation.controller;

import com.raul.transferservice.application.dto.CreateTransferRequest;
import com.raul.transferservice.application.dto.TransferResponse;
import com.raul.transferservice.application.service.CreateTransferService;
import com.raul.transferservice.domain.model.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final CreateTransferService createTransferService;

    public TransferController(CreateTransferService createTransferService) {
        this.createTransferService = createTransferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> create(
            @RequestHeader("idempotency-key") String idempotencyKey,
            @Valid @RequestBody CreateTransferRequest request
    ) {
        Transfer transfer = createTransferService.execute(
                request.senderId(),
                request.receiverId(),
                request.amount(),
                idempotencyKey
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransferResponse.from(transfer));
    }
}
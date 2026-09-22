CREATE TABLE transfers
(
    id        UUID PRIMARY KEY,
    sender_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    request_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
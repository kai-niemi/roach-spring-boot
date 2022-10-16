package io.roach.spring.idempotency.domain.transaction;

public enum TransactionStatus {
    placed,
    verified,
    cancelled
}

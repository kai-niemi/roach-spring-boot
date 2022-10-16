package io.roach.spring.idempotency.domain.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such transaction")
public class NoSuchTransactionException extends RuntimeException {
    public NoSuchTransactionException(String message) {
        super(message);
    }
}

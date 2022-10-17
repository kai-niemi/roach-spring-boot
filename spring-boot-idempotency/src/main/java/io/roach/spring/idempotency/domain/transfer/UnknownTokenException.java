package io.roach.spring.idempotency.domain.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transfer token not found")
public class UnknownTokenException extends RuntimeException {
    public UnknownTokenException(String message) {
        super(message);
    }
}

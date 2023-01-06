package io.roach.spring.catalog.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown on money calculation with conflicting currencies.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Currency mismatch")
public class CurrencyMismatchException extends IllegalArgumentException {
    public CurrencyMismatchException(String s) {
        super(s);
    }
}

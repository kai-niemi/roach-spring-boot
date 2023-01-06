package io.roach.spring.catalog.common;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Business exception thrown if a referenced account does not exist.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such product")
public class NoSuchProductException extends BusinessException {
    public NoSuchProductException(UUID id) {
        super("No product found with id '" + id + "'");
    }

    public NoSuchProductException(String productRef) {
        super("No product found with reference '" + productRef + "'");
    }
}


package io.roach.spring.catalog.common;

/**
 * Base type for usually recoverable business exceptions.
 */
public abstract class BusinessException extends RuntimeException {
    protected BusinessException(String message) {
        super(message);
    }
}

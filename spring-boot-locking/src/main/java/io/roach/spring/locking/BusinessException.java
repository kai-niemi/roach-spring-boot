package io.roach.spring.locking;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

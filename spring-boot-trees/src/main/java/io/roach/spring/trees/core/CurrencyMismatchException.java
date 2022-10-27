package io.roach.spring.trees.core;

public class CurrencyMismatchException extends IllegalArgumentException {
    public CurrencyMismatchException(String s) {
        super(s);
    }
}

package io.roach.spring.annotations.aspect;

import org.springframework.core.Ordered;

/**
 * Ordering constants for transaction advisors.
 *
 * @author Kai Niemi
 */
public interface AdvisorOrder {
    int LOW = Ordered.LOWEST_PRECEDENCE - 2; // Transaction attributes advice

    int HIGH = Ordered.LOWEST_PRECEDENCE - 3; // Transaction lifecycle advice

    int HIGHEST = Ordered.LOWEST_PRECEDENCE - 4; // Retryable advice
}

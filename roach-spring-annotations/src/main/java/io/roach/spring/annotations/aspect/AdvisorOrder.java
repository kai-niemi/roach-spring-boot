package io.roach.spring.annotations.aspect;

import org.springframework.core.Ordered;

/**
 * Ordering constants for transaction advisors.
 */
public interface AdvisorOrder {
    int WITHIN_CONTEXT = Ordered.LOWEST_PRECEDENCE - 2; // Transaction attributes advice

    int INNER_BOUNDARY = Ordered.LOWEST_PRECEDENCE - 3; // Transaction lifecycle advice

    int OUTER_BOUNDARY = Ordered.LOWEST_PRECEDENCE - 4; // Retryable advice
}

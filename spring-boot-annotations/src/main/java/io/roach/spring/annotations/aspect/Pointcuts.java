package io.roach.spring.annotations.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.roach.spring.annotations.FollowerRead;
import io.roach.spring.annotations.TimeTravel;
import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.annotations.TransactionHints;

/**
 * Shared AOP pointcut expression used across services and components.
 */
@Aspect
public class Pointcuts {
    @Pointcut("@within(transactionBoundary) || @annotation(transactionBoundary)")
    public void anyTransactionBoundaryOperation(TransactionBoundary transactionBoundary) {
    }

    @Pointcut("@annotation(transactionHints)")
    public void anyTransactionHintedOperation(TransactionHints transactionHints) {
    }

    @Pointcut("@annotation(followerRead)")
    public void anyFollowerReadOperation(FollowerRead followerRead) {
    }

    @Pointcut("@annotation(timeTravel)")
    public void anyTimeTravelOperation(TimeTravel timeTravel) {
    }
}

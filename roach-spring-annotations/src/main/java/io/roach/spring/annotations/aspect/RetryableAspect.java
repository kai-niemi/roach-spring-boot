package io.roach.spring.annotations.aspect;

import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.TransactionSystemException;

import io.roach.spring.annotations.TransactionBoundary;

/**
 * AOP around advice that intercepts and retries transient concurrency exceptions such
 * as deadlock looser, pessmistic and optimistic locking failures. Methods matching
 * the pointcut expression (annotated with @TransactionBoundary) are retried a number
 * of times with exponential backoff.
 * <p>
 * NOTE: This advice needs to runs in a non-transactional context, that is before the
 * underlying transaction advisor.
 */
@Aspect
@Order(AdvisorOrder.OUTER_BOUNDARY) // This advisor must be before the TX advisor in the call chain
public class RetryableAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        logger.info("Bootstrapping Retryable aspect");
    }

    @Around(value = "Pointcuts.anyTransactionBoundaryOperation(transactionBoundary)",
            argNames = "pjp,transactionBoundary")
    public Object doInTransaction(ProceedingJoinPoint pjp, TransactionBoundary transactionBoundary)
            throws Throwable {
        // Grab from type if needed (for non-annotated methods)
        if (transactionBoundary == null) {
            transactionBoundary = AopSupport.findAnnotation(pjp, TransactionBoundary.class);
        }

        int numCalls = 0;

        final Instant callTime = Instant.now();

        do {
            try {
                numCalls++;
                Object rv = pjp.proceed();
                if (numCalls > 1) {
                    logger.info(
                            "Transient error recovered after " + numCalls + " of " + transactionBoundary
                                    .retryAttempts() + " retries ("
                                    + Duration.between(callTime, Instant.now()).toString() + ")");
                }
                return rv;
            } catch (TransientDataAccessException | TransactionSystemException ex) { // TX abort on commit's
                Throwable cause = NestedExceptionUtils.getMostSpecificCause(ex);
                if (cause instanceof SQLException) {
                    SQLException sqlException = (SQLException) cause;
                    if ("40001".equals(sqlException.getSQLState())) { // Transient error code
                        handleTransientException(sqlException, numCalls, pjp.getSignature().toShortString(),
                                transactionBoundary.maxBackoff());
                        continue;
                    }
                }

                throw ex;
            } catch (UndeclaredThrowableException ex) {
                Throwable t = ex.getUndeclaredThrowable();
                while (t instanceof UndeclaredThrowableException) {
                    t = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
                }

                Throwable cause = NestedExceptionUtils.getMostSpecificCause(ex);
                if (cause instanceof SQLException) {
                    SQLException sqlException = (SQLException) cause;
                    if ("40001".equals(sqlException.getSQLState())) { // Transient error code
                        handleTransientException(sqlException, numCalls, pjp.getSignature().toShortString(),
                                transactionBoundary.maxBackoff());
                        continue;
                    }
                }
                throw ex;
            }
        } while (numCalls < transactionBoundary.retryAttempts());

        throw new ConcurrencyFailureException("Too many transient errors (" + numCalls + ") for method ["
                + pjp.getSignature().toShortString() + "]. Giving up!");
    }

    private void handleTransientException(SQLException ex, int numCalls, String method, long maxBackoff) {
        try {
            long backoffMillis = Math.min((long) (Math.pow(2, numCalls) + Math.random() * 1000), maxBackoff);
            if (numCalls <= 1 && logger.isWarnEnabled()) {
                logger.warn("Transient error (backoff {}ms) in call {} to '{}': {}",
                        backoffMillis, numCalls, method, ex.getMessage());
            }
            Thread.sleep(backoffMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

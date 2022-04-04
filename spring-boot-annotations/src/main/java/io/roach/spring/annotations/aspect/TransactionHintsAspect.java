package io.roach.spring.annotations.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.annotations.TransactionHint;
import io.roach.spring.annotations.TransactionHints;

/**
 * Aspect with an around advice that sets the transaction attributes declared by a
 * transaction boundary, such as time outs.
 * <p>
 * NOTE: This advice needs to runs in a transactional context, that is after the
 * underlying transaction advisor and also any retry advisors.
 *
 * @author Kai Niemi
 */
@Aspect
@Order(AdvisorOrder.LOW)
public class TransactionHintsAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Around(value = "Pointcuts.anyTransactionBoundaryOperation(transactionBoundary)",
            argNames = "pjp,transactionBoundary")
    public Object aroundTransactionalMethod(ProceedingJoinPoint pjp, TransactionBoundary transactionBoundary)
            throws Throwable {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");

        // Grab from type if needed (for non-annotated methods)
        if (transactionBoundary == null) {
            transactionBoundary = AopSupport.findAnnotation(pjp, TransactionBoundary.class);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Transaction attributes applied for {}: {}",
                    pjp.getSignature().toShortString(),
                    transactionBoundary);
        }

        applyVariables(transactionBoundary);

        return pjp.proceed();
    }

    private void applyVariables(TransactionBoundary transactionBoundary) {
        if (!"".equals(transactionBoundary.applicationName())) {
            jdbcTemplate.update("SET application_name=?", transactionBoundary.applicationName());
        }

        if (!TransactionBoundary.Priority.normal.equals(transactionBoundary.priority())) {
            jdbcTemplate.execute("SET TRANSACTION PRIORITY " + transactionBoundary.priority().name());
        }

        if (!TransactionBoundary.Vectorize.auto.equals(transactionBoundary.vectorize())) {
            jdbcTemplate.execute("SET vectorize='" + transactionBoundary.vectorize().name() + "'");
        }

        if (transactionBoundary.timeout() > 0) {
            jdbcTemplate.update("SET statement_timeout=?", transactionBoundary.timeout() * 1000);
        }

        if (transactionBoundary.readOnly()) {
            jdbcTemplate.execute("SET transaction_read_only=true");
        }

        if (transactionBoundary.followerRead()) {
            if ("(exact)".equals(transactionBoundary.followerReadStaleness())) {
                jdbcTemplate.execute(
                        "SET TRANSACTION AS OF SYSTEM TIME follower_read_timestamp()");
            } else {
                throw new UnsupportedOperationException("Bounded staleness reads must use implicit transactions");
//                jdbcTemplate.execute(
//                        "SET TRANSACTION AS OF SYSTEM TIME with_max_staleness('"
//                                + transactionBoundary.followerReadStaleness() + "')");
            }
        } else {
            if (!"(none)".equals(transactionBoundary.timeTravel())) {
                jdbcTemplate.update("SET TRANSACTION AS OF SYSTEM TIME INTERVAL '"
                        + transactionBoundary.timeTravel() + "'");
            }
        }
    }

    @Around(value = "Pointcuts.anyTransactionHintedOperation(transactionHints)",
            argNames = "pjp,transactionHints")
    public Object aroundTransactionalMethod(ProceedingJoinPoint pjp, TransactionHints transactionHints)
            throws Throwable {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");

        if (logger.isTraceEnabled()) {
            logger.trace("Transaction hints applied for {}: {}",
                    pjp.getSignature().toShortString(),
                    transactionHints);
        }

        for (TransactionHint hint : transactionHints.value()) {
            if (hint.intValue() >= 0) {
                jdbcTemplate.update("SET " + hint.name() + "=" + hint.intValue());
            } else {
                jdbcTemplate.update("SET " + hint.name() + "=?", hint.value());
            }
        }

        return pjp.proceed();
    }
}

package io.roach.spring.annotations.aspect;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.annotations.TransactionHint;
import io.roach.spring.annotations.TransactionHints;

/**
 * This advisor must be after retry and TX advisors in the call chain (in a transactional context)
 */
@Aspect
@Order(AdvisorOrder.WITHIN_CONTEXT)
public class TransactionHintsAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${info.build.artifact}")
    private String applicationName;

    @PostConstruct
    public void init() {
        logger.info("Bootstrapping Transaction Hints aspect");
    }

    @Around(value = "Pointcuts.anyTransactionBoundaryOperation(transactionBoundary)",
            argNames = "pjp,transactionBoundary")
    public Object doInTransaction(ProceedingJoinPoint pjp, TransactionBoundary transactionBoundary)
            throws Throwable {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");

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
        if ("(default)".equals(transactionBoundary.applicationName())) {
            jdbcTemplate.update("SET application_name=?", applicationName);
        } else if (!"".equals(transactionBoundary.applicationName())) {
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
    }

    @Around(value = "Pointcuts.anyTransactionHintedOperation(transactionHints)",
            argNames = "pjp,transactionHints")
    public Object doInTransactionHinted(ProceedingJoinPoint pjp, TransactionHints transactionHints)
            throws Throwable {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");

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

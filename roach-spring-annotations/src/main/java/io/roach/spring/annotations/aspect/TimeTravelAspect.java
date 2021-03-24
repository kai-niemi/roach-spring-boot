package io.roach.spring.annotations.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TimeTravel;

/**
 * Aspect that sets a transaction attribute signaling a time travel query.
 * <p>
 * https://www.cockroachlabs.com/docs/stable/as-of-system-time
 *
 * @author Kai Niemi
 */
@Aspect
@Order(AdvisorOrder.WITHIN_CONTEXT)
public class TimeTravelAspect {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before(value = "Pointcuts.anyTimeTravelOperation(timeTravel)", argNames = "timeTravel")
    public void beforeTimeTravelOperation(TimeTravel timeTravel) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");
        jdbcTemplate.update("SET TRANSACTION AS OF SYSTEM TIME INTERVAL '" + timeTravel.interval() + "'");
    }
}

package io.roach.spring.annotations.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.FollowerRead;

/**
 * Aspect that sets a transaction attribute signaling a follower read.
 * <p>
 * https://www.cockroachlabs.com/docs/stable/follower-reads.html
 *
 * @author Kai Niemi
 */
@Aspect
@Order(AdvisorOrder.LOW)
public class FollowerReadAspect {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before(value = "@annotation(followerRead)", argNames = "followerRead")
    public void beforeFollowerReadOperation(FollowerRead followerRead) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");
        if ("(exact)".equals(followerRead.staleness())) {
            jdbcTemplate.execute(
                    "SET TRANSACTION AS OF SYSTEM TIME follower_read_timestamp()");
        } else {
            jdbcTemplate.execute(
                    "SET TRANSACTION AS OF SYSTEM TIME with_max_staleness('" + followerRead.staleness() + "')");
        }
    }
}

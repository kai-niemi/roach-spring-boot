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
 *
 * https://www.cockroachlabs.com/docs/stable/follower-reads.html
 *
 * @author Kai Niemi
 */
@Aspect
@Order(AdvisorOrder.WITHIN_CONTEXT)
public class FollowerReadAspect {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before(value = "Pointcuts.anyFollowerReadOperation(followerRead)", argNames = "followerRead")
    public void beforeFollowerReadOperation(FollowerRead followerRead) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");
        jdbcTemplate.execute("SET TRANSACTION AS OF SYSTEM TIME experimental_follower_read_timestamp()");
    }
}

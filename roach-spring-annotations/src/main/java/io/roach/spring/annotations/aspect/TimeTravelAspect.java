package io.roach.spring.annotations.aspect;

import javax.annotation.PostConstruct;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TimeTravel;

@Aspect
@Order(AdvisorOrder.WITHIN_CONTEXT)
public class TimeTravelAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        logger.info("Bootstrapping TimeTravel aspect");
    }

    @Before(value = "Pointcuts.anyTimeTravelOperation(timeTravel)", argNames = "timeTravel")
    public void beforeTimeTravelQuery(TimeTravel timeTravel) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(),
                "TX not active - explicit transaction required");
        jdbcTemplate.update("SET TRANSACTION AS OF SYSTEM TIME INTERVAL '" + timeTravel.interval() + "'");
    }
}

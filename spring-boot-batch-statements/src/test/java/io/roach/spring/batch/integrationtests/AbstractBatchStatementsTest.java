package io.roach.spring.batch.integrationtests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.spring.batch.AbstractIntegrationTest;
import io.roach.spring.batch.service.OrderService;

public abstract class AbstractBatchStatementsTest extends AbstractIntegrationTest {
    protected final int numProducts = 250;

    protected final int numCustomers = 1000;

    @Autowired
    protected OrderService orderService;

    @BeforeAll
    public void setupTest() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive(), "TX active");

        testDoubles.deleteTestDoubles();
        testDoubles.createProducts(numProducts, product -> {});
        testDoubles.createCustomers(numCustomers);
    }
}

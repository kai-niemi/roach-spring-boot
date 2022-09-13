package io.roach.spring.outbox;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.outbox.domain.TransactionService;

//@ActiveProfiles(value = {"test","verbose"})
@ActiveProfiles(value = {"test"})
public class OutboxTest extends AbstractOutboxTest {
    @Autowired
    private TransactionService transactionService;

    @Test
    @Order(1)
    public void whenCreatingOne_thenSucceed() {
        generate(1, batch -> batch.forEach(instance -> transactionService.createTransaction(instance)));
    }

    @Test
    @Order(2)
    public void whenCreatingSingletons_thenSucceed() {
        generate(numAccounts, batch -> {
            batch.forEach(singleton -> {
                transactionService.createTransaction(singleton);
            });
        });
    }

    @Test
    @Order(3)
    public void whenCreatingBatches_thenSucceed() {
        generate(numAccounts, batch -> {
            transactionService.createTransactionCollection(batch);
        });
    }
}


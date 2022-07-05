package io.roach.spring.outbox;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.outbox.domain.TransactionService;

@ActiveProfiles(value = {"test","verbose"})
public class TransactionTest extends AbstractTransactionTest {
    @Autowired
    private TransactionService transactionService;

//    @Test
//    @Order(1)
//    public void whenCreatingSingletons_thenSucceed() {
//        generate(batch -> {
//            batch.forEach(singleton -> {
//                transactionService.createTransaction(singleton);
//            });
//        });
//    }

    @Test
    @Order(2)
    public void whenCreatingBatches_thenSucceed() {
        generate(batch -> {
            transactionService.createTransactionCollection(batch);
        });
    }
}


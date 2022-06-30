package io.roach.spring.transaction;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class ExplicitTransactionTest extends AbstractTransactionTest {
    @Autowired
    @Qualifier("jdbcTransferService")
    private TransferService transactionService;

    @Test
    @Order(1)
    public void whenCreatingTransactions_thenUseExplicitTransactions() {
        generate(batch -> {
            batch.forEach(singleton -> {
                transactionService.createTransfer(singleton);
            });
        });
    }

    @Test
    @Order(2)
    public void whenCreatingTransactions_thenUseExplicitBatchTransactions() {
        generate(batch -> {
            transactionService.createTransferCollection(batch);
        });
    }
}

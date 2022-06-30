package io.roach.spring.transaction;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class ImplicitTransactionTest extends AbstractTransactionTest {
    @Autowired
    @Qualifier("CommonTableExpressionTransferService")
    private TransferService transactionService;

    @Test
    @Order(1)
    public void whenCreatingTransactions_thenUseCTE_andImplicitTransactions() {
        generate(batch -> {
            batch.forEach(singleton -> {
                transactionService.createTransfer(singleton);
            });
        });
    }

    @Test
    @Order(2)
    public void whenCreatingTransactions_thenUseCTE_andImplicitBatchTransactions() {
        generate(batch -> {
            transactionService.createTransferCollection(batch);
        });
    }

//    @Test
//    @Order(3)
//    public void whenXX() {
//        AccountEntity account = newAccount();
//        accountService.persist(account);
//    }
}


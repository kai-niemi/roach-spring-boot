package io.roach.spring.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.roach.spring.transactions.domain.AccountEntity;
import io.roach.spring.transactions.domain.JdbcAccountService;
import io.roach.spring.transactions.domain.TransactionEntity;
import io.roach.spring.transactions.domain.TransferService;

public abstract class AbstractTransferTest extends AbstractTest {
    protected static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    protected JdbcAccountService accountService;

    protected final int numAccounts = 100;

    protected final int numTransfersPerAccount = 64;

    protected final int batchSize = 32;

    @BeforeAll
    public void setupTest() {
        logger.info("Clearing all accounts and transactions");
        accountService.clearAll();

        logger.info("Creating {} accounts using batch size {}", numAccounts, batchSize);

        final List<AccountEntity> batch = new ArrayList<>();

        int n = numAccounts;
        while (n > 0) {
            LongStream.rangeClosed(1, batchSize).forEach(value -> batch.add(newAccount()));
            accountService.create(batch);
            n -= batchSize;
            batch.clear();
        }
    }

    protected AccountEntity newAccount() {
        AccountEntity instance = new AccountEntity();
        instance.setBalance(RANDOM.nextDouble(10.00, 500.00));
        return instance;
    }

    protected TransactionEntity newTransaction(Long accountId) {
        TransactionEntity instance = new TransactionEntity();
        instance.setAccountId(accountId);
        instance.setAmount(RANDOM.nextDouble(10.00, 500.00));
        instance.setTransactionType("credit");
        instance.setTransactionStatus("hello");
        return instance;
    }

    protected void generate(Consumer<List<TransactionEntity>> consumer) {
        logger.info("Creating {} transactions per account ({} accounts)", numTransfersPerAccount, numAccounts);

        AtomicInteger numAccounts = new AtomicInteger();
        AtomicInteger numTransactions = new AtomicInteger();

        long t = System.currentTimeMillis();

        accountService.findAll().forEach(account -> {
            List<TransactionEntity> batch = new ArrayList<>();

            IntStream.rangeClosed(1, numTransfersPerAccount)
                    .forEach(value -> batch.add(newTransaction(account.getId())));

            consumer.accept(batch);

            numAccounts.incrementAndGet();
            numTransactions.addAndGet(batch.size());
        });

        logger.info("Created {} transactions total at {} ms per account",
                numTransactions.get(),
                (System.currentTimeMillis() - t) / numAccounts.get());
    }

//    @Test
//    @Order(1)
//    public void whenCreatingSingletons_thenSucceed() {
//        generate(batch -> {
//            batch.forEach(singleton -> {
//                getTransferService().createTransfer(singleton);
//            });
//        });
//    }
//
    @Test
    @Order(2)
    public void whenCreatingBatches_thenSucceed() {
        generate(batch -> {
            getTransferService().createTransferCollection(batch);
        });
    }

    protected abstract TransferService getTransferService();
}

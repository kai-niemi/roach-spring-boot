package io.roach.spring.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTransactionTest extends AbstractTest {
    protected static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    protected AccountService accountService;

    protected final int numAccounts = 10;

    protected final int numTransactionsPerAccount = 10;

    protected final int batchSize = 64;

    @BeforeAll
    public void setupTest() {
        logger.info("Clearing all accounts");
        accountService.clearAll();

        final List<AccountEntity> batch = new ArrayList<>();

        logger.info("Creating {} accounts using batch size {}", numAccounts, batchSize);

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
        instance.setBalance(RANDOM.nextDouble());
        return instance;
    }

    protected TransactionEntity newTransaction(Long accountId) {
        TransactionEntity instance = new TransactionEntity();
        instance.setAccountId(accountId);
        instance.setAmount(RANDOM.nextDouble());
        instance.setTransactionType("credit");
        instance.setTransactionStatus("hello");
        return instance;
    }

    protected void generate(Consumer<List<TransactionEntity>> consumer) {
        logger.info("Creating {} transactions per account", numTransactionsPerAccount);

        AtomicInteger numAccounts = new AtomicInteger();
        AtomicInteger numTransactions = new AtomicInteger();

        long t = System.currentTimeMillis();
        accountService.findAll().forEach(account -> {
            List<TransactionEntity> batch = new ArrayList<>();

            IntStream.rangeClosed(1, numTransactionsPerAccount)
                    .forEach(value -> batch.add(newTransaction(account.getId())));

            consumer.accept(batch);

            numAccounts.incrementAndGet();
            numTransactions.addAndGet(batch.size());
        });

        logger.info("Created {} transactions total at {} ms per account", numTransactions.get(),
                (System.currentTimeMillis() - t) / numAccounts.get());
    }

}

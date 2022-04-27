package io.roach.spring.cte;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SpringBootTest(classes = {TestApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class ExplicitVsImplicitTest {
    private static int spinner = 0;

    private static void tick() {
        System.out.printf(Locale.US, "\r(%s)", "|/-\\".toCharArray()[spinner++ % 4]);
        System.out.flush();
    }

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountCreationService;

    @Autowired
    @Qualifier("transactionServiceExplicit")
    private TransactionService transactionServiceExplicit;

    @Autowired
    @Qualifier("transactionServiceImplicit")
    private TransactionService transactionServiceImplicit;

    @BeforeAll
    public void setupTest() {
        accountCreationService.clearAll();
    }

    public AccountEntity newAccount() {
        AccountEntity instance = new AccountEntity();
        instance.setBalance(RANDOM.nextDouble());
        return instance;
    }

    public TransactionEntity newTransaction(Long accountId) {
        TransactionEntity instance = new TransactionEntity();
        instance.setAccountId(accountId);
        instance.setAmount(RANDOM.nextDouble());
        instance.setTransactionType("credit");
        instance.setTransactionStatus("hello");
        return instance;
    }

    int numAccounts = 100;

    int numTransactionsPerAccount = 10;

    @Test
    @Order(1)
    public void whenCreatingAccounts_thenUseBatching() {
        final List<AccountEntity> batch = new ArrayList<>();

        final int batchSize = 64;

        logger.info("Creating {} accounts using batch size {}", numAccounts, batchSize);

        while (numAccounts > 0) {
            LongStream.rangeClosed(1, batchSize).forEach(value -> batch.add(newAccount()));
            accountCreationService.create(batch);

            numAccounts -= batchSize;
            batch.clear();
        }
    }

    @Test
    @Order(2)
    public void whenCreatingTransactions_thenUseExplicitTransactions() {
        generate(batch -> {
            batch.forEach(singleton -> {
                transactionServiceExplicit.createTransaction(singleton);
            });
        });
    }

    @Test
    @Order(3)
    public void whenCreatingTransactions_thenUseCTE_andImplicitTransactions() {
        generate(batch -> {
            batch.forEach(singleton -> {
                transactionServiceImplicit.createTransaction(singleton);
            });
        });
    }

    @Test
    @Order(4)
    public void whenCreatingTransactions_thenUseExplicitBatchTransactions() {
        generate(batch -> {
            transactionServiceExplicit.createTransactions(batch);
        });
    }

    @Test
    @Order(5)
    public void whenCreatingTransactions_thenUseCTE_andImplicitBatchTransactions() {
        generate(batch -> {
            transactionServiceImplicit.createTransactions(batch);
        });
    }

    private void generate(Consumer<List<TransactionEntity>> consumer) {
        logger.info("Creating {} transactions per account", numTransactionsPerAccount);

        AtomicInteger numAccounts = new AtomicInteger();
        AtomicInteger numTransactions = new AtomicInteger();

        long t = System.currentTimeMillis();
        accountRepository.findAll().forEach(account -> {
            List<TransactionEntity> batch = new ArrayList<>();

            IntStream.rangeClosed(1, numTransactionsPerAccount).forEach(value -> batch.add(newTransaction(account.getId())));

            consumer.accept(batch);

            numAccounts.incrementAndGet();
            numTransactions.addAndGet(batch.size());
        });

        logger.info("Created {} transactions total at {} ms per account",
                numTransactions.get(), (System.currentTimeMillis() - t) / numAccounts.get());
    }
}

package io.roach.spring.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.spring.identity.model.AccountEntity;

@SpringBootTest(classes = {IdentityApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public abstract class AbstractAccountTest<T extends AccountEntity<ID>, ID> {
    protected static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected JpaRepository<T, ID> accountRepository;

    @BeforeAll
    public void clearAccounts() {
        accountRepository.deleteAll();
    }

    @Test
    public void whenCreatingEntities_thenUseSingleton() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        T e1 = accountRepository.save(createSingleton());
        Assertions.assertNotNull(e1.getId());
        Assertions.assertFalse(e1.isNew());

        T e2 = accountRepository.save(createSingleton());
        Assertions.assertNotNull(e2.getId());
        Assertions.assertFalse(e2.isNew());
    }

    @Test
    public void whenCreatingEntities_thenUseBatch() {
        int numAccounts = 100;
        int batchSize = 16;

        Assertions.assertTrue(numAccounts > batchSize);
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        while (numAccounts > 0) {
            List<T> batch = createBatch(batchSize);

            accountRepository.saveAll(batch).forEach(e -> {
                Assertions.assertNotNull(e.getId());
                Assertions.assertFalse(e.isNew());
            });

            numAccounts -= batchSize;
            if (numAccounts < batchSize) {
                batchSize = Math.min(numAccounts, batchSize);
            }
        }
    }

    private List<T> createBatch(int size) {
        List<T> batch = new ArrayList<>();
        LongStream.rangeClosed(1, size).forEach(value -> batch.add(createSingleton()));
        return batch;
    }

    private T createSingleton() {
        T instance = newInstance();
        instance.setBalance(RANDOM.nextDouble(10.00, 500.00));
        return instance;
    }

    protected abstract T newInstance();
}

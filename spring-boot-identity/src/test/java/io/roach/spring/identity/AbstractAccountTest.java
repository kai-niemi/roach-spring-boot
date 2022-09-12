package io.roach.spring.identity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import io.roach.spring.identity.model.AccountEntity;

@SpringBootTest(classes = {IdentityApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public abstract class AbstractAccountTest<T extends AccountEntity<ID>, ID> {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    protected JpaRepository<T, ID> accountRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private static final int numAccounts = 1_000;

    @BeforeAll
    public void clearAccounts() {
        accountRepository.deleteAllInBatch();
    }

    @Order(1)
    @Test
    public void whenCreatingEntities_thenUseSingletonInserts() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        IntStream.rangeClosed(1, numAccounts).forEach(value -> {
            T e = accountRepository.save(createSingleton());
            Assertions.assertNotNull(e.getId());
            Assertions.assertFalse(e.isNew());
        });
    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32, 64, 128})
    public void whenCreatingEntities_thenUseBatchInserts(int batchSize) {
        Assertions.assertTrue(numAccounts > batchSize);
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        int n = numAccounts;
        while (n > 0) {
            List<T> batch = createBatch(batchSize);

            accountRepository.saveAll(batch).forEach(e -> {
                Assertions.assertNotNull(e.getId());
                Assertions.assertFalse(e.isNew());
            });

            n -= batchSize;
            batchSize = Math.min(n, batchSize);
        }
    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32, 64, 128})
    public void whenUpdatingEntities_thenUseBatchUpdates(int batchSize) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        final AtomicReference<Pageable> pageRef = new AtomicReference<>(PageRequest.of(0, batchSize));

        while (pageRef.get().isPaged()) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                Assertions.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());

                Page<T> page = accountRepository.findAll(pageRef.get());
                if (!page.isEmpty()) {
                    page.forEach(this::updateInstance);
                }
                pageRef.set(page.nextPageable());
            });
        }
    }

    private List<T> createBatch(int size) {
        List<T> batch = new ArrayList<>();
        LongStream.rangeClosed(1, size).forEach(value -> batch.add(createSingleton()));
        return batch;
    }

    private static String randomName(int min) {
        byte[] buffer = new byte[min];
        RANDOM.nextBytes(buffer);
        return ENCODER.encodeToString(buffer);
    }

    protected T createSingleton() {
        T instance = newInstance();
        instance.setBalance(RANDOM.nextDouble(100.00, 5000.00));
        instance.setCurrency("USD");
        instance.setName(randomName(32));
        instance.setDescription(randomName(64));
        instance.setCreationTime(LocalDateTime.now());
        return instance;
    }

    protected T newInstance() {
        throw new UnsupportedOperationException("Not implemented");
    }

    protected void updateInstance(T instance) {
        instance.setClosed(!instance.isClosed());
        instance.setUpdatedTime(LocalDateTime.now());
    }
}

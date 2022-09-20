package io.roach.spring.pooling;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest(classes = {TestApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
//@ActiveProfiles({"test"})
public class AccountTest {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    private AccountService accountService;

    @BeforeAll
    public void clearAccounts() {
        accountService.deleteAll();
    }

    @Order(1)
    @ParameterizedTest
    @ValueSource(ints = {2, 4, 8, 16, 32})
    public void whenCreatingEntities_thenUseSingletonInserts(int numAccounts) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        IntStream.rangeClosed(1, numAccounts).forEach(value -> {
            AccountEntity e = accountService.createOne(createInstance());
            Assertions.assertNotNull(e.getId());
            Assertions.assertFalse(e.isNew());
        });
    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32, 64, 128})
    public void whenCreatingEntities_thenUseBatchInserts(int batchSize) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        int n = 256;
        while (n > 0) {
            List<AccountEntity> batch = createBatch(batchSize);

            accountService.createAll(batch).forEach(e -> {
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
    public void whenUpdatingEntities_thenUseBatchUpdates(int pageSize) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        Pageable pageable = PageRequest.of(0, pageSize);
        do {
            Page<AccountEntity> page = accountService.findPage(pageable);
            page.forEach(this::updateInstance);
            pageable = page.nextPageable();
        } while (pageable.isPaged());
    }

    private List<AccountEntity> createBatch(int size) {
        List<AccountEntity> batch = new ArrayList<>();
        LongStream.rangeClosed(1, size).forEach(value -> batch.add(createInstance()));
        return batch;
    }

    private AccountEntity createInstance() {
        AccountEntity instance = new AccountEntity();
        instance.setBalance(RANDOM.nextDouble(100.00, 5000.00));
        instance.setCurrency("USD");
        instance.setName(randomName(32));
        instance.setDescription(randomName(64));
        instance.setCreationTime(LocalDateTime.now());
        return instance;
    }

    private void updateInstance(AccountEntity instance) {
        instance.setClosed(!instance.isClosed());
        instance.setUpdatedTime(LocalDateTime.now());
    }

    private static String randomName(int min) {
        byte[] buffer = new byte[min];
        RANDOM.nextBytes(buffer);
        return ENCODER.encodeToString(buffer);
    }
}

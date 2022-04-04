package io.roach.spring.locking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ProfileNames.CRDB_DEV, ProfileNames.VERBOSE})
//@ActiveProfiles({ProfileNames.PSQL_DEV, ProfileNames.VERBOSE})
public class LockingTest extends AbstractIntegrationTest {
    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    @Autowired
    private ProductService productService;

    @BeforeAll
    public void setupTest() {
        testDoubles.deleteTestDoubles();

        testDoubles.createProducts(10, 10_000, product -> {
            logger.info("{}", product.toString());
        });
    }

    @Test
    @Order(1)
    public void whenModifyingInventoryWithOptimisticLocking_thenRetryOnConflicts() {
        final UUID id = productService.findOneProduct().getId();

        runConcurrently(THREADS, 100, () -> {
            logger.debug("Update {}", id);
            productService.updateProductWithOptimisticLock(id, -1);
        });
    }

    @Test
    @Order(2)
    public void whenModifyingInventoryWithPessimisticLocking_thenAvoidConflicts() {
        final UUID id = productService.findOneProduct().getId();

        runConcurrently(THREADS, 1000, () -> {
            logger.debug("Update {}", id);
            productService.updateProductWithPessimisticLock(id, -1);
//            productService.updateProduct(id, -1);
        });
    }

    private void runConcurrently(int threads, int iterations, Runnable worker) {
        List<CompletableFuture<?>> allFutures = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(iterations);

        try {
            logger.info("Scheduling {} workers", threads);

            IntStream.rangeClosed(1, threads).forEach(value -> {
                CompletableFuture<?> f = CompletableFuture.runAsync(() -> {
                    while (counter.decrementAndGet() > 0) {
                        worker.run();
                    }
                });
                allFutures.add(f);
            });

            logger.info("Waiting for all {} workers", threads);
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[] {})).join();
        } catch (CancellationException | CompletionException e) {
            logger.error("", e);
        }
    }
}

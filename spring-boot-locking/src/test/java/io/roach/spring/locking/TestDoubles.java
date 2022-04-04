package io.roach.spring.locking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionBoundary;

@Service
public class TestDoubles {
    @Autowired
    private ProductRepository productRepository;

    @TransactionBoundary
    public void deleteTestDoubles() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        productRepository.deleteAllInBatch();
    }

    @TransactionBoundary
    public void createProducts(int numProducts, int quantity, Consumer<Product> callback) {
        IntStream.rangeClosed(1, numProducts).forEach(value -> {
            Product p = productRepository.save(newProduct(quantity));
            callback.accept(p);
        });
    }

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public Product newProduct(int quantity) {
        return Product.builder()
                .withName("CockroachDB Unleashed - First Edition")
                .withPrice(new BigDecimal(random.nextDouble(10, 500))
                        .setScale(2, RoundingMode.HALF_UP))
                .withSku(UUID.randomUUID().toString())
                .withQuantity(quantity)
                .build();
    }
}

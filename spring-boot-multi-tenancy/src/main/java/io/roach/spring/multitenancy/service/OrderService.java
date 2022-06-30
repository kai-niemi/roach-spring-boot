package io.roach.spring.multitenancy.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.multitenancy.domain.Product;
import io.roach.spring.multitenancy.repository.OrderRepository;
import io.roach.spring.multitenancy.repository.ProductRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @TransactionBoundary(followerRead = true)
    public BigDecimal getTotalOrderPrice() {
        Assert.isTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Not read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return orderRepository.getTotalOrderPrice();
    }

    @TransactionBoundary(readOnly = true)
    public Product findProductBySku(String sku) {
        Assert.isTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Not read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        Optional<Product> p1 = productRepository.findProductBySkuNoLock(sku);
        Product p = p1.orElseThrow(() -> new IllegalArgumentException("Not found"));

        // Silly attempt to "accidentally" update, but notice its being ignored
        p.setPrice(BigDecimal.ZERO);
        productRepository.saveAndFlush(p);

        return p;
    }
}

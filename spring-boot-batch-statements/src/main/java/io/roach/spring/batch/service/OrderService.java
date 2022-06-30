package io.roach.spring.batch.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.batch.domain.Order;
import io.roach.spring.batch.domain.Product;
import io.roach.spring.batch.domain.ShipmentStatus;
import io.roach.spring.batch.repository.OrderRepository;
import io.roach.spring.batch.repository.ProductRepository;

@Service
public class OrderService {
    private static <T> Stream<List<T>> chunkedStream(Stream<T> stream, int chunkSize) {
        AtomicInteger idx = new AtomicInteger();
        return stream.collect(Collectors.groupingBy(x -> idx.getAndIncrement() / chunkSize))
                .values().stream();
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @TransactionBoundary
    public void placeOrder(Order order) {
        Assert.isTrue(!TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        orderRepository.save(order);
    }

    @TransactionBoundary
    public void placeOrderAndUpdateInventory(Collection<Order> orders) {
        Stream<List<Order>> chunked = chunkedStream(orders.stream(), 150);
        chunked.forEach(chunk -> {
            chunk.forEach(this::placeOrderAndUpdateInventory);
            orderRepository.flush();
        });
    }

    @TransactionBoundary
    public void placeOrderAndUpdateInventory(Order order) {
        Assert.isTrue(!TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        // Update product inventories
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.addInventoryQuantity(-orderItem.getQuantity());
            productRepository.save(product); // product is in detached state
        });
        order.setStatus(ShipmentStatus.confirmed);

        orderRepository.save(order);
    }

    @TransactionBoundary
    public void placeOrders(Collection<Order> orders) {
        Assert.isTrue(!TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        orderRepository.saveAll(orders);
    }

    @TransactionBoundary
    public void updateOrderStatus(Iterable<UUID> ids, ShipmentStatus status) {
        Assert.isTrue(!TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        orderRepository.findAllById(ids).forEach(order -> order.setStatus(status));
    }

    @TransactionBoundary(readOnly = true)
    public List<UUID> findOrderIdsByStatus(ShipmentStatus status) {
        return orderRepository.findIdsByShipmentStatus(status);
    }

    @TransactionBoundary(readOnly = true)
    public List<Order> findOrdersByStatus(ShipmentStatus status, int limit) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        List<Order> orders = orderRepository.findByShipmentStatus(status,
                Pageable.ofSize(limit)).getContent();
        // Lazy-fetch associations
        orders.forEach(order -> order.getOrderItems().size());
        return orders;
    }
}

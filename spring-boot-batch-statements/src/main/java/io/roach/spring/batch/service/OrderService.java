package io.roach.spring.batch.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

    @TransactionBoundary
    public List<UUID> findOrderIdsWithStatus(ShipmentStatus status) {
        return orderRepository.findIdsByShipmentStatus(status);
    }
}

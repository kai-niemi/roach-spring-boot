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
import io.roach.spring.batch.domain.ShipmentStatus;
import io.roach.spring.batch.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @TransactionBoundary
    public void placeOrder(Order order) {
        Assert.isTrue(!TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Read-only");
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
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

package io.roach.spring.multitenancy.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionService;
import io.roach.spring.multitenancy.domain.Order;
import io.roach.spring.multitenancy.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @TransactionService
    public BigDecimal getTotalOrderPrice() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        BigDecimal price = BigDecimal.ZERO;
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            price = price.add(order.getTotalPrice());
        }

        return price;
    }
}

package io.roach.spring.batch.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.spring.batch.AbstractIntegrationTest;
import io.roach.spring.batch.TestDoubles;
import io.roach.spring.batch.domain.Order;
import io.roach.spring.batch.domain.ShipmentStatus;
import io.roach.spring.batch.service.OrderService;
import io.roach.spring.batch.util.Timer;

public abstract class AbstractBatchStatementsTest extends AbstractIntegrationTest {
    private final int numProducts = 250;

    private final int numCustomers = 1000;

    @Autowired
    private TestDoubles testDoubles;

    @Autowired
    private OrderService orderService;

    @BeforeAll
    public void setupTest() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive(), "TX active");

        testDoubles.deleteTestDoubles();
        testDoubles.createProducts(numProducts);
        testDoubles.createCustomers(numCustomers);
    }

    @org.junit.jupiter.api.Order(1)
    @ParameterizedTest
    @ValueSource(ints = {10, 250, 500, 1500})
    public void whenCreatingSingletonOrders_thenSucceed(int numOrders) {
        Timer.timeExecution("placeOrder_singleton",
                () -> testDoubles.newOrders(numOrders, 4, order -> orderService.placeOrder(order)));
    }

    @org.junit.jupiter.api.Order(2)
    @ParameterizedTest
    @ValueSource(ints = {10, 250, 500, 1500})
    public void whenCreatingBatchOrders_thenSucceed(int numOrders) {
        final List<Order> orders = new ArrayList<>();
        testDoubles.newOrders(numOrders, 4, orders::add);

        Timer.timeExecution("placeOrders_batch",
                () -> orderService.placeOrders(orders));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void whenFindingPlacedOrders_thenUpdateStatusToConfirmed() {
        final List<UUID> ids = orderService.findOrderIdsWithStatus(ShipmentStatus.placed);
        Timer.timeExecution("updateOrderStatus->confirmed",
                () -> orderService.updateOrderStatus(ids, ShipmentStatus.confirmed));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void whenFindingConfirmedOrders_thenUpdateStatusToDelivered() {
        final List<UUID> ids = orderService.findOrderIdsWithStatus(ShipmentStatus.confirmed);
        Timer.timeExecution("updateOrderStatus->delivered",
                () -> orderService.updateOrderStatus(ids, ShipmentStatus.delivered));
    }
}

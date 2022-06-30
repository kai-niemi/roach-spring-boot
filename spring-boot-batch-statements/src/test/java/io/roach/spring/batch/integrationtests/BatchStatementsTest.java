package io.roach.spring.batch.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.batch.ProfileNames;
import io.roach.spring.batch.domain.Order;
import io.roach.spring.batch.domain.ShipmentStatus;
import io.roach.spring.batch.util.Timer;

@ActiveProfiles(value = {ProfileNames.CRDB_DEV},inheritProfiles = false)
public class BatchStatementsTest extends AbstractBatchStatementsTest {
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
        final List<UUID> ids = orderService.findOrderIdsByStatus(ShipmentStatus.placed);
        Timer.timeExecution("updateOrderStatus->confirmed",
                () -> orderService.updateOrderStatus(ids, ShipmentStatus.confirmed));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void whenFindingConfirmedOrders_thenUpdateStatusToDelivered() {
        final List<UUID> ids = orderService.findOrderIdsByStatus(ShipmentStatus.confirmed);
        Timer.timeExecution("updateOrderStatus->delivered",
                () -> orderService.updateOrderStatus(ids, ShipmentStatus.delivered));
    }
}

package io.roach.spring.batch.integrationtests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.batch.ProfileNames;
import io.roach.spring.batch.util.Timer;

@ActiveProfiles({ProfileNames.CRDB_DEV, ProfileNames.VERBOSE})
public class BatchStatementsVerboseTest extends AbstractBatchStatementsTest {
    @org.junit.jupiter.api.Order(1)
    @ParameterizedTest
    @ValueSource(ints = {10, 20, 50, 100})
    public void whenCreatingSingletonOrders_thenSucceed(int numOrders) {
        Timer.timeExecution("placeOrder_singleton",
                () -> testDoubles.newOrders(numOrders, 4, order -> orderService.placeOrder(order)));
    }

    @org.junit.jupiter.api.Order(2)
    @ParameterizedTest
    @ValueSource(ints = {10, 20, 50, 100})
    public void whenCreatingSingletonOrders_thenAlsoUpdateInventory(int numOrders) {
        Timer.timeExecution("placeOrder_singleton",
                () -> testDoubles.newOrders(numOrders, 4, order -> orderService.placeOrderAndUpdateInventory(order)));
    }
}

package io.roach.spring.batch.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.batch.ProfileNames;
import io.roach.spring.batch.domain.Order;
import io.roach.spring.batch.util.Timer;

@ActiveProfiles(value = {ProfileNames.CRDB_DEV})
//@ActiveProfiles(value = {ProfileNames.CRDB_DEV, ProfileNames.DISABLE_BATCH})
public class BenchmarkTest extends AbstractBatchStatementsTest {
    @org.junit.jupiter.api.Order(1)
    @ParameterizedTest
    @ValueSource(ints = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000})
    public void whenCreatingBulkOrders_thenAlsoUpdateInventory(int numOrders) {
        final List<Order> orders = new ArrayList<>();
        testDoubles.newOrders(numOrders, 4, orders::add);

        Timer.timeExecution("placeOrderAndUpdateInventory(" + numOrders + ")",
                () -> orderService.placeOrderAndUpdateInventory(orders));
    }
}

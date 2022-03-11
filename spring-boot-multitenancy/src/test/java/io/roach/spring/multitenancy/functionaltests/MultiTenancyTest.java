package io.roach.spring.multitenancy.functionaltests;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.multitenancy.AbstractIntegrationTest;
import io.roach.spring.multitenancy.ProfileNames;
import io.roach.spring.multitenancy.TestDoubles;
import io.roach.spring.multitenancy.aspect.TenantScope;
import io.roach.spring.multitenancy.config.TenantName;
import io.roach.spring.multitenancy.config.TenantRegistry;
import io.roach.spring.multitenancy.domain.Product;
import io.roach.spring.multitenancy.repository.CustomerRepository;
import io.roach.spring.multitenancy.repository.OrderRepository;
import io.roach.spring.multitenancy.repository.ProductRepository;

@ActiveProfiles({ProfileNames.CLOUD})
public class MultiTenancyTest extends AbstractIntegrationTest {
    private final int numProducts = 100;

    private final int numCustomers = 500;

    private final int numOrders = 100;

    @Autowired
    private TestDoubles testDoubles;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    public void setupTest() {
        Arrays.stream(TenantName.values()).forEach(tenant -> {
            TenantRegistry.setTenantId(tenant);
            testDoubles.removeTestDoubles();
        });
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(1)
    @TenantScope(TenantName.alpha)
    public void whenCreatingProductInventory_thenStoreInSingleTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");

        List<Product> products = IntStream.rangeClosed(1, numProducts)
                .mapToObj(value -> testDoubles.newProduct())
                .collect(Collectors.toList());

        productRepository.saveAll(products);
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(2)
    @TenantScope(TenantName.alpha)
    public void whenRetrievingProductsFromPrimaryTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numProducts, productRepository.count());
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(3)
    @TenantScope(TenantName.bravo)
    public void whenRetrievingProductsFromOtherTenant_thenReturnZeroAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(0, productRepository.count());
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(4)
    @TenantScope
    public void whenCreatingCustomers_thenStoreInPrimaryTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createCustomers();
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(5)
    @TenantScope(TenantName.bravo)
    public void whenCreatingCustomers_thenStoreInSecondTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createCustomers();
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(6)
    @TenantScope(TenantName.caesar)
    public void whenCreatingCustomers_thenStoreInThirdTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createCustomers();
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(7)
    @TenantScope
    public void whenRetrievingCustomersFromPrimaryTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numCustomers, customerRepository.count());
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(8)
    @TenantScope
    public void whenCreatingOrders_thenStoreInPrimaryTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createOrders();
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(9)
    @TenantScope
    public void whenRetrievingOrdersFromPrimaryTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numOrders, orderRepository.count());
        Assertions.assertTrue(orderRepository.getTotalOrderPrice().longValue() > 0);
    }

    private void createCustomers() {
        IntStream.rangeClosed(1, numCustomers).forEach(value -> customerRepository.save(testDoubles.newCustomer()));
    }

    private void createOrders() {
        testDoubles.newOrders(numOrders, order -> {
            orderRepository.save(order);
        });
    }
}

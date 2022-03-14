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
import io.roach.spring.multitenancy.config.Tenant;
import io.roach.spring.multitenancy.config.TenantContext;
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
        Arrays.stream(Tenant.values()).forEach(tenant -> {
            if (tenant.isVersioned()) {
                TenantContext.setTenantId(tenant);
                testDoubles.deleteTestDoubles();
            }
        });
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(1)
    @TenantScope(Tenant.alpha)
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
    @TenantScope(Tenant.alpha)
    public void whenRetrievingProductsFromPrimaryTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numProducts, productRepository.count());
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(3)
    @TenantScope(Tenant.bravo)
    public void whenRetrievingProductsFromOtherTenant_thenReturnZeroAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(0, productRepository.count());
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(4)
    @TenantScope(Tenant.alpha)
    public void whenCreatingCustomers_thenStoreInPrimaryTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createCustomers(numCustomers + 1);
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(5)
    @TenantScope(Tenant.bravo)
    public void whenCreatingCustomers_thenStoreInSecondTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createCustomers(numCustomers + 2);
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(6)
    @TenantScope(Tenant.caesar)
    public void whenCreatingCustomers_thenStoreInThirdTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createCustomers(numCustomers + 3);
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(7)
    @TenantScope(Tenant.alpha)
    public void whenRetrievingCustomersFromPrimaryTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numCustomers + 1, customerRepository.count());
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(7)
    @TenantScope(Tenant.bravo)
    public void whenRetrievingCustomersFromSecondTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numCustomers + 2, customerRepository.count());
    }

    @Test
    @TransactionBoundary(readOnly = true)
    @Order(7)
    @TenantScope(Tenant.caesar)
    public void whenRetrievingCustomersFromThirdTenant_thenReturnCorrectAmount() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        Assertions.assertEquals(numCustomers + 3, customerRepository.count());
    }

    @Test
    @TransactionBoundary
    @Commit
    @Order(8)
    @TenantScope
    public void whenCreatingOrders_thenStoreInPrimaryTenancy() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");
        createOrders(numOrders);
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

    private void createCustomers(int count) {
        IntStream.rangeClosed(1, count).forEach(value -> customerRepository.save(testDoubles.newCustomer()));
    }

    private void createOrders(int count) {
        testDoubles.newOrders(count, order -> {
            orderRepository.save(order);
        });
    }
}

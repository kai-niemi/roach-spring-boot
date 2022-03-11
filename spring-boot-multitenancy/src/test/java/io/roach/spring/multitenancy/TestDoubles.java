package io.roach.spring.multitenancy;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.multitenancy.domain.Customer;
import io.roach.spring.multitenancy.domain.Order;
import io.roach.spring.multitenancy.domain.Product;
import io.roach.spring.multitenancy.repository.CustomerRepository;
import io.roach.spring.multitenancy.repository.OrderRepository;
import io.roach.spring.multitenancy.repository.ProductRepository;
import io.roach.spring.multitenancy.util.RandomData;

@Service
public class TestDoubles {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @TransactionBoundary
    public void removeTestDoubles() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");

        orderRepository.findAll().forEach(order -> orderRepository.deleteOrderItems(order.getId()));
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
    }

    public Product newProduct() {
        return Product.builder()
                .withName("CockroachDB Unleashed - First Edition")
                .withPrice(RandomData.randomMoneyBetween(20, 500, 2))
                .withSku(RandomData.randomWord(12))
                .withQuantity(RandomData.randomInt(5, 15))
                .build();
    }

    public Customer newCustomer() {
        return Customer.builder()
                .withFirstName(RandomData.randomFirstName())
                .withLastName(RandomData.randomLastName())
                .withUserName(UUID.randomUUID().toString())
                .build();
    }

    public void newOrders(int orderCount, Consumer<Order> callback) {
        List<Customer> allCustomers = customerRepository.findAll();
        List<Product> allProducts = productRepository.findAll();

        IntStream.rangeClosed(1, orderCount).forEach(value -> {
            Product p1 = RandomData.selectRandom(allProducts);
            Product p2 = RandomData.selectRandom(allProducts);
            Product p3 = RandomData.selectRandom(allProducts);
            Product p4 = RandomData.selectRandom(allProducts);
            Product p5 = RandomData.selectRandom(allProducts);

            Customer c1 = RandomData.selectRandom(allCustomers);

            Order o = Order.builder()
                    .withCustomer(c1)
                    .andOrderItem()
                    .withProduct(p1)
                    .withQuantity(2)
                    .withProduct(p2)
                    .withQuantity(3)
                    .then()
                    .andOrderItem()
                    .withProduct(p2)
                    .withQuantity(RandomData.randomInt(2, 10))
                    .then()
                    .andOrderItem()
                    .withProduct(p3)
                    .withQuantity(RandomData.randomInt(2, 10))
                    .then()
                    .andOrderItem()
                    .withProduct(p4)
                    .withQuantity(RandomData.randomInt(2, 10))
                    .then()
                    .andOrderItem()
                    .withProduct(p5)
                    .withQuantity(RandomData.randomInt(2, 10))
                    .then()
                    .build();

            callback.accept(o);
        });
    }
}

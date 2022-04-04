package io.roach.spring.pagination.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.annotations.TransactionNotAllowed;
import io.roach.spring.pagination.domain.Customer;
import io.roach.spring.pagination.domain.Order;
import io.roach.spring.pagination.domain.Product;
import io.roach.spring.pagination.util.RandomData;

@Service
public class Doubles {
    public static final int BATCH_SIZE = 128;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @TransactionNotAllowed
    public void deleteTestData() {
        orderRepository.findAll().forEach(order -> orderRepository.deleteOrderItems(order.getId()));
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
    }

    @TransactionBoundary(readOnly = true)
    public boolean isPopulated() {
        return productRepository.count() > 0;
    }

    @TransactionNotAllowed
    public void createProducts(int numProducts) {
        List<Product> products = new ArrayList<>();
        IntStream.rangeClosed(1, numProducts).forEach(value -> {
            products.add(newProduct());
            if (products.size() > BATCH_SIZE) {
                productRepository.saveAllAndFlush(products);
                products.clear();
            }
        });
        productRepository.saveAllAndFlush(products);
    }

    @TransactionBoundary
    public void createCustomers(int numCustomers) {
        List<Customer> customers = new ArrayList<>();
        IntStream.rangeClosed(1, numCustomers).forEach(value -> {
            customers.add(newCustomer());
            if (customers.size() > BATCH_SIZE) {
                customerRepository.saveAllAndFlush(customers);
                customers.clear();
            }
        });
        customerRepository.saveAllAndFlush(customers);
    }

    @TransactionNotAllowed
    public void createOrders(int numOrders) {
        Page<Customer> topCustomers = customerRepository.findAll(PageRequest.of(0, 10_000));
        Page<Product> topProducts = productRepository.findAll(PageRequest.of(0, 10_000));

        List<Order> orders = new ArrayList<>();
        IntStream.rangeClosed(1, numOrders).forEach(value -> {
            orders.add(newOrder(topProducts.getContent(), topCustomers.getContent()));
            if (orders.size() > BATCH_SIZE) {
                orderRepository.saveAllAndFlush(orders);
                orders.clear();
            }
        });
        orderRepository.saveAllAndFlush(orders);
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

    public Order newOrder(List<Product> products, List<Customer> customers) {
        Order.Builder builder = Order.builder()
                .withCustomer(RandomData.selectRandom(customers));
        IntStream.rangeClosed(1, RandomData.randomInt(1, 10))
                .forEach(value -> builder.andOrderItem()
                        .withProduct(RandomData.selectRandom(products))
                        .withQuantity(RandomData.randomInt(1, 10))
                        .then());
        return builder.build();
    }
}

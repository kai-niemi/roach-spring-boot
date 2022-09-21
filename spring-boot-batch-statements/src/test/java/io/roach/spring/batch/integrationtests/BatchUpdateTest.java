package io.roach.spring.batch.integrationtests;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import io.roach.spring.batch.AbstractIntegrationTest;
import io.roach.spring.batch.ProfileNames;
import io.roach.spring.batch.domain.Product;
import io.roach.spring.batch.repository.ProductRepository;

import static java.sql.Statement.SUCCESS_NO_INFO;

@ActiveProfiles({ProfileNames.CRDB_DEV, ProfileNames.VERBOSE})
//@ActiveProfiles({ProfileNames.CRDB_DEV})
public class BatchUpdateTest extends AbstractIntegrationTest {
    private static <T> Stream<List<T>> chunkedStream(Stream<T> stream, int chunkSize) {
        AtomicInteger idx = new AtomicInteger();
        return stream.collect(Collectors.groupingBy(x -> idx.getAndIncrement() / chunkSize))
                .values().stream();
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private JdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;

    //    private final int numProducts = 50_000;
    private final int numProducts = 256 * 5;

    @BeforeAll
    public void setupTest() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);

        logger.info("Deleting all products");
        testDoubles.deleteTestDoubles();

        logger.info("Creating {} products", numProducts);

        int batchSize = 256;
        IntStream.rangeClosed(1, numProducts / batchSize).forEach(value -> {
            testDoubles.createProducts(batchSize, product -> {
                product.setInventory(1);
                product.setPrice(BigDecimal.ONE);
            });
        });
    }

    @Order(1)
    @ParameterizedTest
    @ValueSource(ints = {16, 32, 64, 128, 256, 512, 768, 1024})
    public void whenUpdatingProducts_thenObserveNoBatchUpdates(int batchSize) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive(), "TX active");

        logger.info("Finding all products..");

        Stream<List<Product>> chunked = chunkedStream(productRepository.findAll().stream(), batchSize);

        logger.info("Updating products in batches of {}", batchSize);

        // This doesn't actually get batched over wire in PSQL (like with INSERT rewrites)
        chunked.forEach(chunk -> {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "TX not active");

                int rows[] = jdbcTemplate.batchUpdate("UPDATE products SET inventory=?, price=? WHERE id=?",
                        new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Product product = chunk.get(i);
                                product.addInventoryQuantity(1);
                                product.setPrice(product.getPrice().add(new BigDecimal("1.00")));

                                ps.setInt(1, product.getInventory());
                                ps.setBigDecimal(2, product.getPrice());
                                ps.setObject(3, product.getId());
                            }

                            @Override
                            public int getBatchSize() {
                                return chunk.size();
                            }
                        });

                Arrays.stream(rows).sequential().forEach(value -> {
                    Assertions.assertNotEquals(value, SUCCESS_NO_INFO);
                });
                Assertions.assertEquals(chunk.size(), rows.length);
            });
        });
    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(ints = {16, 32, 64, 128, 256, 512, 768, 1024})
    public void whenUpdatingProductsUsingValues_thenObserveBatchUpdates(int batchSize) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive(), "TX active");

        logger.info("Finding all products..");

        Stream<List<Product>> chunked = chunkedStream(productRepository.findAll().stream(), batchSize);

        logger.info("Updating products in batches of {}", batchSize);

        // This does send a single statement batch over the wire
        chunked.forEach(chunk -> {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                int rows = jdbcTemplate.update(
                        "UPDATE products SET inventory=data_table.new_inventory, price=data_table.new_price "
                                + "FROM "
                                + "(select unnest(?) as id, unnest(?) as new_inventory, unnest(?) as new_price) as data_table "
                                + "WHERE products.id=data_table.id",
                        ps -> {
                            List<Integer> qty = new ArrayList<>();
                            List<BigDecimal> price = new ArrayList<>();
                            List<UUID> ids = new ArrayList<>();

                            chunk.forEach(product -> {
                                qty.add(product.addInventoryQuantity(1));
                                price.add(product.getPrice().add(new BigDecimal("1.00")));
                                ids.add(product.getId());
                            });
                            ps.setArray(1, ps.getConnection()
                                    .createArrayOf("UUID", ids.toArray()));
                            ps.setArray(2, ps.getConnection()
                                    .createArrayOf("BIGINT", qty.toArray()));
                            ps.setArray(3, ps.getConnection()
                                    .createArrayOf("DECIMAL", price.toArray()));
                        });
                Assertions.assertEquals(chunk.size(), rows);
            });
        });
    }

    @Test
    @Order(3)
    public void whenReadingProducts_thenReturnUpdatedValues() {
        productRepository.findAll().forEach(product -> {
            Assertions.assertEquals(8 * 2 + 1, product.getInventory());
            Assertions.assertEquals(new BigDecimal("16.00").add(new BigDecimal("1.00")), product.getPrice());
        });
    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(ints = {16, 32, 64, 128, 256, 512, 768, 1024})
    public void whenUpsertingProducts_thenObserveBulkUpdates(int batchSize) {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive(), "TX active");

        logger.info("Upserting {} products", batchSize);

        List<Product> products = new ArrayList<>();

        IntStream.rangeClosed(1, batchSize).forEach(value -> {
            Product product = testDoubles.newProduct();
            product.setId(UUID.randomUUID());
            product.setInventory(1);
            product.setPrice(BigDecimal.ONE);
            products.add(product);
        });

        List<Product> persistent = productRepository.findAll(Pageable.ofSize(32)).getContent();
        Assertions.assertEquals(32, persistent.size());

        // Add persistent products to tail
        products.addAll(persistent);

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            int rows = jdbcTemplate.update(
                    "INSERT INTO products (id,inventory,price,name,sku) "
                            + "select unnest(?) as id, "
                            + "       unnest(?) as inventory, "
                            + "       unnest(?) as price, "
                            + "       unnest(?) as name, "
                            + "       unnest(?) as sku "
                            + "ON CONFLICT (id) do nothing",
                    ps -> {
                        List<Integer> qty = new ArrayList<>();
                        List<BigDecimal> price = new ArrayList<>();
                        List<UUID> ids = new ArrayList<>();
                        List<String> name = new ArrayList<>();
                        List<String> sku = new ArrayList<>();

                        products.forEach(product -> {
                            qty.add(product.getInventory());
                            price.add(product.getPrice());
                            ids.add(product.getId());
                            name.add(product.getName());
                            sku.add(product.getSku());
                        });
                        ps.setArray(1, ps.getConnection()
                                .createArrayOf("UUID", ids.toArray()));
                        ps.setArray(2, ps.getConnection()
                                .createArrayOf("BIGINT", qty.toArray()));
                        ps.setArray(3, ps.getConnection()
                                .createArrayOf("DECIMAL", price.toArray()));
                        ps.setArray(4, ps.getConnection()
                                .createArrayOf("VARCHAR", name.toArray()));
                        ps.setArray(5, ps.getConnection()
                                .createArrayOf("VARCHAR", sku.toArray()));
                    });
            Assertions.assertEquals(products.size() - 32, rows);
        });
    }

}
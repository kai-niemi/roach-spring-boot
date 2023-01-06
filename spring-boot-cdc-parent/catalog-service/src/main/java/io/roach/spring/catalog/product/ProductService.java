package io.roach.spring.catalog.product;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.roach.spring.catalog.common.NoSuchProductException;

/**
 * Defines the business contract for browsing products and prices.
 */
public interface ProductService {
    void createProductBatch(int batchSize, Supplier<Product> factory);

    Product createProduct(Product product);

    void update(Product product);

    Product getProductById(UUID id) throws NoSuchProductException;

    Product getProductBySku(String productRef) throws NoSuchProductException;

    Page<Product> findProductsPage(Pageable page);

    void delete(UUID id);
}

package io.roach.spring.order.product;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines the business contract for browsing products and prices.
 */
public interface ProductService {
    Product getProductById(UUID id) throws NoSuchProductException;

    Page<Product> findProductsPage(Pageable page);
}

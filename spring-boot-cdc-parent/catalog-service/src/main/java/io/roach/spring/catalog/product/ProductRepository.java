package io.roach.spring.catalog.product;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query(value = "SELECT nextval('product_seq')", nativeQuery = true)
    Integer nextSeqNumber();

    Optional<Product> getBySku(String sku);
}

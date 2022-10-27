package io.roach.spring.trees.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    List<ProductVariation> findByProductId(Long productId);

    ProductVariation getBySku(String skuCode);
}

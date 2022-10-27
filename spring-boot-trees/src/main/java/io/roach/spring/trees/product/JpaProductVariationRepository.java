package io.roach.spring.trees.product;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class JpaProductVariationRepository extends SimpleJpaRepository<ProductVariation, Long>
        implements ProductVariationRepository {

    private final EntityManager em;

    public JpaProductVariationRepository(@Autowired EntityManager em) {
        super(ProductVariation.class, em);
        this.em = em;
    }

    @Override
    public List<ProductVariation> findByProductId(Long productId) {
        return em
                .createNamedQuery(ProductVariation.BY_PRODUCT_ID, ProductVariation.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    public ProductVariation getBySku(String skuCode) {
        return em
                .createNamedQuery(ProductVariation.BY_CODE, ProductVariation.class)
                .setParameter("code", skuCode)
                .getSingleResult();
    }
}


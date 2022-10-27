package io.roach.spring.trees.product;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Embeddable component representing a single product association with a single category
 * with some additional association details.
 * <p/>
 * The CategorizedProduct class represents a join table between product and category.
 * The ER model for this is really a many-to-many association, but instead of two
 * entities and two collections, its mapped as a single collection (in Product)
 * of composite elements, instances of this class.
 * <p/>
 * This simplifies the lifecycle of the association. Navigation is however unidirectional,
 * only possible from Product -> CategorizedProduct -> Category and not in the other direction.
 */
@Embeddable
public class CategorizedProduct {
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    protected CategorizedProduct() {
    }

    public CategorizedProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

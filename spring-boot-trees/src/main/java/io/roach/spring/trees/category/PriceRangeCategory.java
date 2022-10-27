package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("PRICE")
public class PriceRangeCategory extends Category {
    public PriceRangeCategory() {
    }

    public PriceRangeCategory(String name) {
        super(name);
    }

    public PriceRangeCategory(Category parent, String name) {
        super(parent, name);
    }
}

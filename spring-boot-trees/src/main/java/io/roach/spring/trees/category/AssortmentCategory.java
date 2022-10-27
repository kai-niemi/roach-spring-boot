package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("ASSORTMENT")
public class AssortmentCategory extends Category {
    public AssortmentCategory() {
    }

    public AssortmentCategory(String name) {
        super(name);
    }

    public AssortmentCategory(Category parent, String name) {
        super(parent, name);
    }
}

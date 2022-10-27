package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("GRAPE")
public class GrapeCategory extends Category {
    public GrapeCategory() {
    }

    public GrapeCategory(String name) throws NullPointerException {
        super(name);
    }

    public GrapeCategory(Category parent, String name) {
        super(parent, name);
    }
}


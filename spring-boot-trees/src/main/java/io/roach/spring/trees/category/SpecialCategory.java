package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("SPECIAL")
public class SpecialCategory extends Category {
    public SpecialCategory() {
    }

    public SpecialCategory(String name) throws NullPointerException {
        super(name);
    }

    public SpecialCategory(Category parent, String name) {
        super(parent, name);
    }
}


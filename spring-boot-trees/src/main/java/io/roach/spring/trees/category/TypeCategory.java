package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("TYPE")
public class TypeCategory extends Category {
    public TypeCategory() {
    }

    public TypeCategory(String name) throws NullPointerException {
        super(name);
    }

    public TypeCategory(Category parent, String name) {
        super(parent, name);
    }
}


package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("LABEL")
public class LabelCategory extends Category {
    public LabelCategory() {
    }

    public LabelCategory(String name) {
        super(name);
    }

    public LabelCategory(Category parent, String name) {
        super(parent, name);
    }
}

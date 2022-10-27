package io.roach.spring.trees.category;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("PRODUCER")
public class ProducerCategory extends Category {
    public ProducerCategory() {
    }

    public ProducerCategory(String name) throws NullPointerException {
        super(name);
    }

    public ProducerCategory(Category parent, String name) {
        super(parent, name);
    }
}

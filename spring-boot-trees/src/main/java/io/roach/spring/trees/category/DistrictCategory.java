package io.roach.spring.trees.category;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("DISTRICT")
public class DistrictCategory extends Category {
    @Column
    private String country;

    public DistrictCategory() {
    }

    public DistrictCategory(String name) throws NullPointerException {
        super(name);
    }

    public DistrictCategory(Category parent, String name) {
        super(parent, name);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}


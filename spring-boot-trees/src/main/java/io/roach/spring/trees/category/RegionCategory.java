package io.roach.spring.trees.category;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Custom category for beverage products.
 */
@Entity
@DiscriminatorValue("REGION")
public class RegionCategory extends Category {
    @Column
    private String country;

    public RegionCategory() {
    }

    public RegionCategory(String name) {
        super(name);
    }

    public RegionCategory(Category parent, String name) {
        super(parent, name);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

package io.roach.spring.trees.product;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import io.roach.spring.trees.core.AbstractPersistentEntity;
import io.roach.spring.trees.core.Money;

/**
 * Domain object representing a product, expressed in different variations that are
 * organized into different categories.
 */
@Entity
@Table(name = "product")
public class Product extends AbstractPersistentEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_code", length = 128, nullable = false, unique = true)
    private String skuCode;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(length = 2048)
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "name", nullable = false, length = 64)
    private List<String> tags = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Product addTag(String tag) {
        tags.add(tag);
        return this;
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public List<String> getTags() {
        return tags;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public Product setSkuCode(String skuCode) {
        this.skuCode = skuCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductVariation createVariation(String skuCode, Money listPrice) {
        return new ProductVariation.Builder()
                .withProduct(this)
                .withSku(skuCode)
                .withPrice(listPrice)
                .build();
    }
}

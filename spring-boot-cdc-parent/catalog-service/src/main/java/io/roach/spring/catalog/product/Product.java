package io.roach.spring.catalog.product;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.spring.catalog.LinkRelations;
import io.roach.spring.catalog.common.AbstractAuditableEntity;

@Entity
@Table(name = "product")
@DynamicInsert
@DynamicUpdate
@Relation(value = LinkRelations.PRODUCT_REL,
        collectionRelation = LinkRelations.PRODUCTS_REL)
public class Product extends AbstractAuditableEntity<UUID> {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 128)
    private String name;

    @Column(length = 256)
    private String description;

    @NaturalId
    @Column(length = 128, nullable = false, unique = true)
    private String sku;

    @Column(length = 25, nullable = false)
    private BigDecimal price;

    @Column(length = 3)
    private String currency;

    @Column(nullable = false)
    private int inventory;

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public void incInventory(int delta) {
        this.inventory += delta;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Product instance = new Product();

        private Builder() {
        }

        public Builder withName(String name) {
            instance.name = name;
            return this;
        }

        public Builder withSku(String sku) {
            instance.sku = sku;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            instance.price = price;
            return this;
        }

        public Builder withCurrency(String currency) {
            instance.currency = currency;
            return this;
        }

        public Builder withInventory(int quantity) {
            instance.inventory = quantity;
            return this;
        }


        public Product build() {
            return instance;
        }
    }
}

package io.roach.spring.order.product;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.spring.order.LinkRelations;
import io.roach.spring.order.common.AbstractEntity;

@Entity
@Table(name = "product")
@DynamicInsert
@DynamicUpdate
@Relation(value = LinkRelations.PRODUCT_REL,
        collectionRelation = LinkRelations.PRODUCTS_REL)
public class Product extends AbstractEntity<UUID> {
    // Assigned, not generated
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(length = 128)
    private String name;

    @Column(length = 256)
    private String description;

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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", inventory=" + inventory +
                "} " + super.toString();
    }
}

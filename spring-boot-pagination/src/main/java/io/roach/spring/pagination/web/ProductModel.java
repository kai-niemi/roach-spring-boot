package io.roach.spring.pagination.web;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Relation(value = LinkRels.PRODUCT_REL,
        collectionRelation = LinkRels.PRODUCTS_REL)
@JsonPropertyOrder({"links", "embedded"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductModel extends RepresentationModel<ProductModel> {
    private String name;

    private String sku;

    private BigDecimal price;

    private int inventory;

    public String getName() {
        return name;
    }

    public ProductModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getSku() {
        return sku;
    }

    public ProductModel setSku(String sku) {
        this.sku = sku;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public int getInventory() {
        return inventory;
    }

    public ProductModel setInventory(int inventory) {
        this.inventory = inventory;
        return this;
    }
}

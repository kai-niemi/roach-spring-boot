package io.roach.spring.pagination.web;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Relation(value = LinkRels.ORDER_ITEM_REL,
        collectionRelation = LinkRels.ORDERS_ITEMS_REL)
@JsonPropertyOrder({"links", "embedded"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemModel extends RepresentationModel<OrderItemModel> {
    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalCost;

    public int getQuantity() {
        return quantity;
    }

    public OrderItemModel setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public OrderItemModel setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public OrderItemModel setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
        return this;
    }
}

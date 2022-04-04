package io.roach.spring.pagination.web;

import java.math.BigDecimal;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Relation(value = LinkRels.ORDER_REL,
        collectionRelation = LinkRels.ORDERS_REL)
@JsonPropertyOrder({"links", "embedded"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderModel extends RepresentationModel<OrderModel> {
    private BigDecimal totalPrice;

    private CollectionModel<OrderItemModel> orderItems;

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public OrderModel setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public CollectionModel<OrderItemModel> getOrderItems() {
        return orderItems;
    }

    public OrderModel setOrderItems(
            CollectionModel<OrderItemModel> orderItems) {
        this.orderItems = orderItems;
        return this;
    }
}

package io.roach.spring.batch.domain;

import java.math.BigDecimal;
import java.util.function.Consumer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Embeddable
public class OrderItem {
    public static final class NestedBuilder {
        private final Order.Builder parentBuilder;

        private final Consumer<OrderItem> callback;

        private int quantity;

        private BigDecimal unitPrice;

        private Product product;

        NestedBuilder(Order.Builder parentBuilder, Consumer<OrderItem> callback) {
            this.parentBuilder = parentBuilder;
            this.callback = callback;
        }

        public NestedBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public NestedBuilder withUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public NestedBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public Order.Builder then() {
            if (this.unitPrice == null) {
                this.unitPrice = product.getPrice();
            }

            OrderItem orderItem = new OrderItem();
            orderItem.product = this.product;
            orderItem.unitPrice = this.unitPrice;
            orderItem.quantity = this.quantity;

            callback.accept(orderItem);

            return parentBuilder;
        }
    }

    @Column(nullable = false, updatable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, updatable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)  // Default fetch type is EAGER for @ManyToOne
    @JoinColumn(name = "product_id", updatable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal totalCost() {
        if (unitPrice == null) {
            throw new IllegalStateException("unitPrice is null");
        }
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", product=" + product +
                '}';
    }
}

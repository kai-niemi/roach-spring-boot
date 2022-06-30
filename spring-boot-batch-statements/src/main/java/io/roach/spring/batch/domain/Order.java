package io.roach.spring.batch.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "orders")
@TypeDef(name = "custom_enum", typeClass = ShipmentStatusEnumType.class)
public class Order extends AbstractEntity<UUID> {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Customer customer;

        private final List<OrderItem> orderItems = new ArrayList<>();

        private Builder() {
        }

        public Builder withCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public OrderItem.NestedBuilder andOrderItem() {
            return new OrderItem.NestedBuilder(this, orderItems::add);
        }

        public Order build() {
            if (this.customer == null) {
                throw new IllegalStateException("Missing customer");
            }
            if (this.orderItems.isEmpty()) {
                throw new IllegalStateException("Empty order");
            }

            Order order = new Order();
            order.customer = this.customer;
            order.deliveryAddress = customer.getAddress();
            order.totalPrice = order.subTotal();
            order.orderItems.addAll(this.orderItems);
            return order;
        }
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "total_price", nullable = false, updatable = false)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JoinTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "item_pos")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25, nullable = false)
    @Type(type = "custom_enum")
    private ShipmentStatus status = ShipmentStatus.placed;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, updatable = false, name = "date_placed")
    private LocalDateTime datePlaced;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, name = "date_updated")
    private LocalDateTime dateUpdated;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1",
                    column = @Column(name = "deliv_address1")),
            @AttributeOverride(name = "address2",
                    column = @Column(name = "deliv_address2")),
            @AttributeOverride(name = "city",
                    column = @Column(name = "deliv_city")),
            @AttributeOverride(name = "postcode",
                    column = @Column(name = "deliv_postcode")),
            @AttributeOverride(name = "country",
                    column = @Column(name = "deliv_country"))
    })
    private Address deliveryAddress;

    @PrePersist
    protected void onCreate() {
        if (datePlaced == null) {
            datePlaced = LocalDateTime.now();
        }
        if (dateUpdated == null) {
            dateUpdated = LocalDateTime.now();
        }
    }

    @Override
    public UUID getId() {
        return id;
    }

    public Order setStatus(ShipmentStatus status) {
        this.status = status;
        this.dateUpdated = LocalDateTime.now();
        return this;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getDatePlaced() {
        return datePlaced;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public Order setId(UUID id) {
        this.id = id;
        return this;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public BigDecimal subTotal() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItem oi : orderItems) {
            subTotal = subTotal.add(oi.totalCost());
        }
        return subTotal;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", totalPrice=" + totalPrice +
                ", customer=" + customer +
                ", orderItems=" + orderItems +
                ", status=" + status +
                ", datePlaced=" + datePlaced +
                ", dateUpdated=" + dateUpdated +
                ", deliveryAddress=" + deliveryAddress +
                '}';
    }
}

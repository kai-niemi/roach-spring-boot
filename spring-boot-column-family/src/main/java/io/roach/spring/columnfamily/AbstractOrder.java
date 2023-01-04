package io.roach.spring.columnfamily;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractOrder extends AbstractEntity<Long> {
    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, name = "date_placed")
    private LocalDate datePlaced;

    @Column(name = "bill_to_first_name")
    private String billToFirstName;

    @Column(name = "bill_to_last_name")
    private String billToLastName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1",
                    column = @Column(name = "bill_address1", length = 255)),
            @AttributeOverride(name = "address2",
                    column = @Column(name = "bill_address2", length = 255)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "bill_city", length = 255)),
            @AttributeOverride(name = "postcode",
                    column = @Column(name = "bill_postcode", length = 16)),
            @AttributeOverride(name = "country",
                    column = @Column(name = "bill_country", length = 16))
    })
    private Address billAddress;

    @Column(name = "deliv_to_first_name")
    private String deliverToFirstName;

    @Column(name = "deliv_to_last_name")
    private String deliverToLastName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1",
                    column = @Column(name = "deliv_address1", length = 255)),
            @AttributeOverride(name = "address2",
                    column = @Column(name = "deliv_address2", length = 255)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "deliv_city", length = 255)),
            @AttributeOverride(name = "postcode",
                    column = @Column(name = "deliv_postcode", length = 16)),
            @AttributeOverride(name = "country",
                    column = @Column(name = "deliv_country", length = 16))
    })
    private Address deliveryAddress;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 64)
    private OrderStatus orderStatus = OrderStatus.PLACED;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatePlaced() {
        return datePlaced;
    }

    public void setDatePlaced(LocalDate datePlaced) {
        this.datePlaced = datePlaced;
    }

    public String getBillToFirstName() {
        return billToFirstName;
    }

    public void setBillToFirstName(String billToFirstName) {
        this.billToFirstName = billToFirstName;
    }

    public String getBillToLastName() {
        return billToLastName;
    }

    public void setBillToLastName(String billToLastName) {
        this.billToLastName = billToLastName;
    }

    public Address getBillAddress() {
        return billAddress;
    }

    public void setBillAddress(Address billAddress) {
        this.billAddress = billAddress;
    }

    public String getDeliverToFirstName() {
        return deliverToFirstName;
    }

    public void setDeliverToFirstName(String deliverToFirstName) {
        this.deliverToFirstName = deliverToFirstName;
    }

    public String getDeliverToLastName() {
        return deliverToLastName;
    }

    public void setDeliverToLastName(String deliverToLastName) {
        this.deliverToLastName = deliverToLastName;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void incrementTotalPrice(BigDecimal increment) {
        this.totalPrice = this.totalPrice.add(increment);
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus status) {
        this.orderStatus = status;
    }
}


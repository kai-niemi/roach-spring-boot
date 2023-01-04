package io.roach.spring.columnfamilies;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "purchase_order1")
@NamedQuery(name = "Order1.findByIdForUpdateStatus",
        query = "SELECT o.id,o.orderStatus,o.totalPrice FROM Order1 o WHERE o.id = ?1")
@NamedQuery(name = "Order1.findByIdForUpdatePrice",
        query = "SELECT o.id,o.totalPrice,o.orderStatus FROM Order1 o WHERE o.id = ?1")
@DynamicInsert
@DynamicUpdate
public class Order1 extends AbstractOrder {
}


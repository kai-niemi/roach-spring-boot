package io.roach.spring.columnfamily;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "purchase_order2")
@NamedQuery(name = "Order2.findByIdForUpdateStatus",
        query = "SELECT o.id,o.orderStatus FROM Order2 o WHERE o.id = ?1")
@NamedQuery(name = "Order2.findByIdForUpdatePrice",
        query = "SELECT o.id,o.totalPrice FROM Order2 o WHERE o.id = ?1")
@DynamicInsert
@DynamicUpdate
public class Order2 extends AbstractOrder {
}

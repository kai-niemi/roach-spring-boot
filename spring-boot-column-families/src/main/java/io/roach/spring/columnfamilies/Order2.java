package io.roach.spring.columnfamilies;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "purchase_order2")
@DynamicInsert
@DynamicUpdate
public class Order2 extends AbstractOrder {
}

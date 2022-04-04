package io.roach.spring.pagination.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.spring.pagination.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Modifying
    @Query(value = "delete from order_items where order_id=:orderId", nativeQuery = true)
    void deleteOrderItems(@Param("orderId") UUID orderId);

    @Query(value = "select sum(o.totalPrice) from Order o")
    BigDecimal getTotalOrderPrice();

    @Query(value = "select o from Order o "
            + "join o.customer c "
            + "where c.id=:customerId",
            countQuery = "select count(o.id) from Order o "
                    + "join o.customer c "
                    + "where c.id=:customerId")
    Page<Order> findOrdersByCustomerId(Pageable pageable, @Param("customerId") UUID customerId);

    @Query(value = "select o from Order o "
            + "join o.orderItems oi "
            + "where oi.product.id=:productId",
            countQuery = "select count(o.id) from Order o "
                    + "join o.orderItems oi "
                    + "where oi.product.id=:productId")
    Page<Order> findOrdersByProductId(Pageable pageable, @Param("productId") UUID productId);
}

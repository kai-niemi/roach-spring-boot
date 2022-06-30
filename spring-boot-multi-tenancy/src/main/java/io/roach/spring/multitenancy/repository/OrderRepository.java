package io.roach.spring.multitenancy.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.spring.multitenancy.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Modifying
    @Query(value = "delete from order_items where order_id=:orderId", nativeQuery = true)
    void deleteOrderItems(@Param("orderId") UUID orderId);

    @Query(value = "from Order o "
            + "join fetch o.customer c "
            + "where c.userName=:userName")
    List<Order> findOrdersByUserName(@Param("userName") String userName);

    @Query(value = "select sum(o.totalPrice) from Order o")
    BigDecimal getTotalOrderPrice();
}

package io.roach.spring.batch.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.spring.batch.domain.Order;
import io.roach.spring.batch.domain.ShipmentStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Modifying
    @Query(value = "delete from order_items where 1=1", nativeQuery = true)
    void deleteAllOrderItems();

    @Query(value = "select o.id from Order o where o.status=:status")
    List<UUID> findIdsByShipmentStatus(@Param("status") ShipmentStatus status);
}

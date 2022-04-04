package io.roach.spring.locking;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.spring.annotations.TransactionService;

@Repository
@TransactionService
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select p from Product p where p.id=:id")
    Product getByIdWithPessimisticLock(@Param("id") UUID id);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select p from Product p where p.id=:id")
    Optional<Product> getByIdOptimisticLock(@Param("id") UUID id);

    @Modifying
    @Query("update Product p set p.inventory = p.inventory + :inventory where p.id=:id")
    void updateInventory(@Param("id") UUID id, @Param("inventory") int qty);
}

package io.roach.spring.pooling.product;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    @Query(value = "select sum(p.inventory) "
            + "from product p "
            + "as of system time follower_read_timestamp() " // Requires implicit read-only tx
            + "where p.for_sale = true", nativeQuery = true)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    Long sumTotalInventoryJdbc();

    @Query(value = "select sum(p.inventory) "
            + "from ProductEntity p "
            + "where p.forSale = true")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    Long sumTotalInventoryJpql();

    @Modifying
    @Query("delete from ProductEntity p where p.inventory = 0")
    void deleteExpiredProducts();
}

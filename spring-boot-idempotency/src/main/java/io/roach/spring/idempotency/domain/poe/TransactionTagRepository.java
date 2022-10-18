package io.roach.spring.idempotency.domain.poe;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.idempotency.domain.transaction.TransactionCollectionTag;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface TransactionTagRepository extends JpaRepository<TransactionCollectionTag, UUID> {
    @Query(value = "UPDATE poe_tag "
            + "SET crdb_internal_expiration = current_timestamp()\\:\\:\\:TIMESTAMPTZ + '5 minutes'\\:\\:INTERVAL "
            + "WHERE id = :id "
            + "RETURNING 1",
            nativeQuery = true)
    int increaseTTLInterval(@Param("id") UUID id);
}

package io.roach.spring.idempotency.domain.poe;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.idempotency.domain.transaction.TransactionCollectionTag;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface TransactionTagRepository extends JpaRepository<TransactionCollectionTag, UUID> {
}

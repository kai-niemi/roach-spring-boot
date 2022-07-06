package io.roach.spring.transactions.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.SUPPORTS)
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Transactional(propagation = Propagation.SUPPORTS)
    TransactionEntity save(TransactionEntity entity);
}

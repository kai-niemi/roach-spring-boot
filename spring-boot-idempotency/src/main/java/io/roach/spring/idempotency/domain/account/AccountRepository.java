package io.roach.spring.idempotency.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.SUPPORTS)
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    @Query(value = "update account "
            + "set balance = balance + ?2 "
            + "where id = ?1 "
            + "returning balance",
            nativeQuery = true)
    Double addAmount(Long id, Double balance);
}

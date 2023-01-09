package io.roach.spring.json;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.spring.json.model.AccountJournal;

@Repository
public interface AccountJournalRepository extends JpaRepository<AccountJournal, UUID> {
    @Query(value = "SELECT * FROM journal WHERE event_type='ACCOUNT'"
            + " AND (payload ->> 'balance')::::decimal BETWEEN :lowerBound AND :upperBound",
            nativeQuery = true)
    List<AccountJournal> findWithBalanceBetween(
            @Param("lowerBound") BigDecimal lowerBound, @Param("upperBound") BigDecimal upperBound);
}

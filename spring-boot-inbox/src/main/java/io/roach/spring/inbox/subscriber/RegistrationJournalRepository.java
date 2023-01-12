package io.roach.spring.inbox.subscriber;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationJournalRepository extends JpaRepository<RegistrationJournal, UUID> {
    @Query(value
            = "SELECT * FROM journal WHERE event_type='CUSTOMER_REG'"
            + " AND (payload ->> 'jurisdiction') = :jurisdiction ORDER BY sequence_no",
            nativeQuery = true,
            countQuery
                    = "SELECT count (1) FROM journal WHERE event_type='CUSTOMER_REG'"
                    + " AND (payload ->> 'jurisdiction') = :jurisdiction")
    Page<RegistrationJournal> findEventsPageWithJurisdiction(Pageable pageable,
                                                             @Param("jurisdiction") String jurisdiction);
}

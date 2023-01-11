package io.roach.spring.inbox.subscriber;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationJournalRepository extends JpaRepository<RegistrationJournal, UUID> {
    @Query(value = "SELECT * FROM journal WHERE event_type='CUSTOMER_REG'"
            + " AND (payload ->> 'jurisdiction') = :jurisdiction",
            nativeQuery = true)
    List<RegistrationJournal> findEventsWithJurisdiction(@Param("jurisdiction") String jurisdiction);
}

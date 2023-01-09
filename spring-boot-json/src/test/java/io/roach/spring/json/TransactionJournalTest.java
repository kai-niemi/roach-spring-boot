package io.roach.spring.json;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.json.model.Transaction;
import io.roach.spring.json.model.TransactionJournal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionJournalTest extends AbstractIntegrationTest {
    @Autowired
    private TransactionJournalRepository transactionJournalRepository;

    private volatile String transactionId;

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    @Order(1)
    public void whenCreatingTransactionEventInJournal_thenComputedKeyIsReturnedFromPayload() {
        Transaction transaction = Transaction.builder()
                .withGeneratedId()
                .withBookingDate(LocalDate.now().minusDays(1))
                .withTransferDate(LocalDate.now())
                .andItem()
                .withAmount(BigDecimal.valueOf(-50.00))
                .withNote("Debit A")
                .then()
                .andItem()
                .withAmount(BigDecimal.valueOf(50.00))
                .withNote("Credit A")
                .then()
                .build();

        TransactionJournal journal = new TransactionJournal();
        journal.setTag("cashout");
        journal.setEvent(transaction);

        journal = transactionJournalRepository.save(journal);

        assertNotNull(journal);
        assertEquals(transaction.getId().toString(), journal.getId());

        transactionId = transaction.getId().toString();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    @Order(2)
    public void whenFindByTag_thenAtLeastOneEventIsReturned() {
        List<TransactionJournal> result = transactionJournalRepository.findByTag("cashout");

        assertTrue(result.stream()
                .map(TransactionJournal::getId)
                .anyMatch(id -> Objects.equals(transactionId, id)));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    @Order(3)
    public void whenFindByTransferDateBetween_thenAtLeastOneEventIsReturned() {
        List<TransactionJournal> result = transactionJournalRepository
                .findTransactionsInDateRange(LocalDate.now().toString(), LocalDate.now().toString());

        assertTrue(result.stream()
                .map(TransactionJournal::getId)
                .anyMatch(id -> Objects.equals(transactionId, id)));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    @Order(4)
    public void whenComputeTransactionLegSum_thenZeroIsReturned() {
        BigDecimal result = transactionJournalRepository
                .sumTransactionLegAmounts("cashout");

        assertEquals(BigDecimal.ZERO.setScale(1), result);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    @Order(5)
    public void whenCreatingBatchesOfTransactionEventJournals_thenDoNothing() {
        List<TransactionJournal> journals = new ArrayList<>();

        IntStream.rangeClosed(1, 500).forEach(value -> {
            Transaction transaction = Transaction.builder()
                    .withGeneratedId()
                    .withBookingDate(LocalDate.now().minusDays(1))
                    .withTransferDate(LocalDate.now())
                    .andItem()
                    .withAmount(BigDecimal.valueOf(-50.00))
                    .withNote("Debit A")
                    .then()
                    .andItem()
                    .withAmount(BigDecimal.valueOf(50.00))
                    .withNote("Credit A")
                    .then()
                    .build();

            TransactionJournal journal = new TransactionJournal();
            journal.setTag("cashout");
            journal.setEvent(transaction);

            journals.add(journal);
        });

        transactionJournalRepository.saveAll(journals);
    }
}

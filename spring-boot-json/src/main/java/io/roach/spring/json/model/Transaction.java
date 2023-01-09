package io.roach.spring.json.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Transaction {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID transactionId;

        private LocalDate bookingDate;

        private LocalDate transferDate;

        private List<TransactionItem> items = new ArrayList<>();

        public Builder withGeneratedId() {
            withId(UUID.randomUUID());
            return this;
        }

        public Builder withId(UUID id) {
            this.transactionId = id;
            return this;
        }

        public Builder withBookingDate(LocalDate bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }

        public Builder withTransferDate(LocalDate transferDate) {
            this.transferDate = transferDate;
            return this;
        }

        public TransactionItem.Builder andItem() {
            return TransactionItem.builder(this, item -> items.add(item));
        }

        public Transaction build() {
            return new Transaction(transactionId, bookingDate, transferDate, items);
        }
    }

    private UUID id;

    private LocalDate transferDate;

    private LocalDate bookingDate;

    private List<TransactionItem> items;

    public Transaction() {
    }

    protected Transaction(UUID id,
                          LocalDate bookingDate,
                          LocalDate transferDate,
                          List<TransactionItem> items) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.transferDate = transferDate;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public List<TransactionItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}

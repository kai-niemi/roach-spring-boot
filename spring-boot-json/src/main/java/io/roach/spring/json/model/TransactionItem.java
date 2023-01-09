package io.roach.spring.json.model;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

public class TransactionItem {
    public static Builder builder(Transaction.Builder parentBuilder, Consumer<TransactionItem> callback) {
        return new Builder(parentBuilder, callback);
    }

    public static class Builder {
        private final Transaction.Builder parentBuilder;

        private final Consumer<TransactionItem> callback;

        private BigDecimal amount;

        private BigDecimal runningBalance;

        private String note;

        private Builder(Transaction.Builder parentBuilder, Consumer<TransactionItem> callback) {
            this.parentBuilder = parentBuilder;
            this.callback = callback;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withRunningBalance(BigDecimal runningBalance) {
            this.runningBalance = runningBalance;
            return this;
        }

        public Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public Transaction.Builder then() {
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setAmount(amount);
            transactionItem.setRunningBalance(runningBalance);
            transactionItem.setNote(note);

            callback.accept(transactionItem);

            return parentBuilder;
        }
    }

    private UUID id;

    private BigDecimal amount;

    private BigDecimal runningBalance;

    private String note;

    protected TransactionItem() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(BigDecimal runningBalance) {
        this.runningBalance = runningBalance;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

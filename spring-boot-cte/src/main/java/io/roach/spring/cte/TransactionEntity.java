package io.roach.spring.cte;

import java.time.LocalDateTime;

import javax.persistence.Id;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "t_transaction")
public class TransactionEntity extends AbstractEntity<Long> {
    @Id
    @Column("id")
    private long id;

    @Column("account_id")
    private long accountId;

    @Column("amount")
    private double amount;

    @Column("transaction_type")
    private String transactionType;

    @Column("transaction_status")
    private String transactionStatus;

    @Column("creation_time")
    private LocalDateTime creationTime;

    @Override
    public Long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public TransactionEntity setId(long id) {
        this.id = id;
        return this;
    }

    public TransactionEntity setAccountId(long accountId) {
        this.accountId = accountId;
        return this;
    }

    public TransactionEntity setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public TransactionEntity setTransactionType(String transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public TransactionEntity setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
        return this;
    }

    public TransactionEntity setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
        return this;
    }
}

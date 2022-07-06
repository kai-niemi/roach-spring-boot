package io.roach.spring.transactions.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "t_transaction")
public class TransactionEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "creation_time", nullable = false, insertable = false)
    private LocalDateTime creationTime;

    @Override
    public Long getId() {
        return id;
    }

    public Long getAccountId() {
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

    public TransactionEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public TransactionEntity setAccountId(Long accountId) {
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

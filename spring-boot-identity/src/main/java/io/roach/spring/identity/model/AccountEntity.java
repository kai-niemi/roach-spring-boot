package io.roach.spring.identity.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AccountEntity<ID> extends AbstractEntity<ID> {
    @Column(name = "balance")
    private double balance;

    @Column(name = "creation_time", updatable = false)
    private LocalDateTime creationTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(length = 3)
    private String currency;

    @Column(length = 128)
    private String name;

    @Column(length = 256)
    private String description;

    @Column
    private boolean closed;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return "AccountEntity{" + "id=" + getId() + ", balance=" + balance + ", creationTime=" + creationTime + '}';
    }
}

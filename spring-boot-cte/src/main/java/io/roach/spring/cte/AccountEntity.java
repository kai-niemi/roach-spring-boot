package io.roach.spring.cte;

import java.time.LocalDateTime;

import javax.persistence.Id;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "t_account")
public class AccountEntity extends AbstractEntity<Long> {
    @Id
    @Column("id")
    private long id;

    @Column("balance")
    private double balance;

    @Column("creation_time")
    private LocalDateTime creationTime;

    @Override
    public Long getId() {
        return id;
    }

    public AccountEntity setId(long id) {
        this.id = id;
        return this;
    }

    public double getBalance() {
        return balance;
    }

    public AccountEntity setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public AccountEntity setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
        return this;
    }
}

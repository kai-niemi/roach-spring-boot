package io.roach.spring.outbox.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "t_account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AccountEntity extends AbstractEntity<Long> {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance")
    private double balance;

    @Column(name = "creation_time", nullable = true, insertable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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

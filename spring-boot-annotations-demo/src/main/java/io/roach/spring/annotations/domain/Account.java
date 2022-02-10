package io.roach.spring.annotations.domain;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 25, nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(length = 25, nullable = false)
    private BigDecimal balance;

    protected Account() {
    }

    public Account(Long id, String name, AccountType type, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}


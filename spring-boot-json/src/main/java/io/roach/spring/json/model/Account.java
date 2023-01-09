package io.roach.spring.json.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Account instance = new Account();

        public Builder withGeneratedId() {
            withId(UUID.randomUUID());
            return this;
        }

        public Builder withId(UUID id) {
            this.instance.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.instance.name = name;
            return this;
        }

        public Builder withBalance(BigDecimal balance) {
            this.instance.balance = balance;
            return this;
        }

        public Builder withAccountType(String accountType) {
            this.instance.accountType = accountType;
            return this;
        }

        public Account build() {
            return instance;
        }
    }

    private UUID id;

    private String name;

    private String accountType;

    private LocalDateTime updated;

    private BigDecimal balance;

    protected Account() {
    }

    protected Account(UUID id, String name, String accountType, LocalDateTime updated, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.accountType = accountType;
        this.updated = updated;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account)) {
            return false;
        }

        Account that = (Account) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

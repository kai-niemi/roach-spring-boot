package io.roach.spring.annotations.controller;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

import io.roach.spring.annotations.domain.AccountType;

public class TransferRequest extends RepresentationModel<TransferRequest> {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TransferRequest instance = new TransferRequest();

        private Builder() {
        }

        public Builder setName(String name) {
            instance.name = name;
            return this;
        }

        public Builder setAccountType(AccountType accountType) {
            instance.accountType = accountType;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            instance.amount = amount;
            return this;
        }

        public TransferRequest build() {
            return instance;
        }
    }

    private String name;

    private AccountType accountType;

    private BigDecimal amount;

    private TransferRequest() {
    }

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

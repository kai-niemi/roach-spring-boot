package io.roach.spring.idempotency.domain.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.hateoas.RepresentationModel;

public class TransferRequest extends RepresentationModel<TransferRequest> {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TransferRequest instance = new TransferRequest();

        public AccountLegBuilder addLeg() {
            return new AccountLegBuilder(this, instance.legs::add);
        }

        public TransferRequest build() {
            if (instance.legs.size() < 2) {
                throw new IllegalStateException("At least 2 legs are required");
            }
            return instance;
        }
    }

    public static class AccountLegBuilder {
        private final AccountLeg instance = new AccountLeg();

        private final Builder parentBuilder;

        private final Consumer<AccountLeg> callback;

        private AccountLegBuilder(Builder parentBuilder, Consumer<AccountLeg> callback) {
            this.parentBuilder = parentBuilder;
            this.callback = callback;
        }

        public AccountLegBuilder withId(Long id) {
            this.instance.id = id;
            return this;
        }

        public AccountLegBuilder withAmount(Double amount) {
            this.instance.amount = amount;
            return this;
        }

        public AccountLegBuilder withBalance(Double balance) {
            this.instance.balance = balance;
            return this;
        }

        public Builder then() {
            if (instance.id == null) {
                throw new IllegalStateException("id is required");
            }
            callback.accept(instance);
            return parentBuilder;
        }
    }

    public static class AccountLeg {
        private Long id;

        private Double balance;

        private Double amount;

        public Long getId() {
            return id;
        }

        public Double getBalance() {
            return balance;
        }

        public Double getAmount() {
            return amount;
        }
    }

    private final List<AccountLeg> legs = new ArrayList<>();

    protected TransferRequest() {
    }

    public List<AccountLeg> getLegs() {
        return Collections.unmodifiableList(legs);
    }
}

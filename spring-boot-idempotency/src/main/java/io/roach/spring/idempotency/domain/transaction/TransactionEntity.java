package io.roach.spring.idempotency.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.roach.spring.idempotency.domain.account.AccountEntity;
import io.roach.spring.idempotency.domain.common.AbstractEntity;
import io.roach.spring.idempotency.domain.common.LocalDateTimeDeserializer;
import io.roach.spring.idempotency.domain.common.LocalDateTimeSerializer;

@Entity
@Table(name = "transaction")
@Relation(value = "transaction",
        collectionRelation = "transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TransactionEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AccountEntity account;

    @Column(name = "amount")
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal amount;

    @Column(name = "type")
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false, insertable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String transactionType) {
        this.type = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus transactionStatus) {
        this.status = transactionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime creationTime) {
        this.createdAt = creationTime;
    }
}

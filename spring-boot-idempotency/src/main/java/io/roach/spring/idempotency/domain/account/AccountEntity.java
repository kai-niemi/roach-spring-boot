package io.roach.spring.idempotency.domain.account;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.roach.spring.idempotency.domain.common.AbstractEntity;
import io.roach.spring.idempotency.domain.common.LocalDateTimeDeserializer;
import io.roach.spring.idempotency.domain.common.LocalDateTimeSerializer;

@Entity
@Table(name = "account")
@Relation(value = "account",
        collectionRelation = "accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@TypeDef(name = "custom_enum", typeClass = ShipmentStatusEnumType.class)
public class AccountEntity extends AbstractEntity<Long> {
//    public class ShipmentStatusEnumType extends org.hibernate.type.EnumType<ShipmentStatus> {
//        @Override
//        public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
//                throws HibernateException, SQLException {
//            st.setObject(index, value != null ? ((Enum<?>) value).name() : null, Types.OTHER);
//        }
//    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(name = "balance")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double balance;

    @Column(name = "created_at", insertable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AccountEntity setCreatedAt(LocalDateTime creationTime) {
        this.createdAt = creationTime;
        return this;
    }
}

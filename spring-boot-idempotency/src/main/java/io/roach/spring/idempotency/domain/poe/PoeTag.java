package io.roach.spring.idempotency.domain.poe;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.roach.spring.idempotency.domain.common.AbstractEntity;
import io.roach.spring.idempotency.domain.common.LocalDateTimeDeserializer;
import io.roach.spring.idempotency.domain.common.LocalDateTimeSerializer;

/**
 * Entity type for storing metadata and return values in post-once-exactly
 * operations.
 *
 * @param <T> the payload or response body recorded as outcome of initial call
 */
@MappedSuperclass
public abstract class PoeTag<T> extends AbstractEntity<UUID> {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "created_at", nullable = false, insertable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "uri")
    private String uri;

    @Column(name = "aggregate_type")
    private String aggregateType;

    @Type(type = "jsonb")
    @Column(name = "body", updatable = false)
    @Basic(fetch = FetchType.LAZY)
    private T body;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}

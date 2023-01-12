package io.roach.spring.inbox.subscriber;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "journal")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "event_type",
        discriminatorType = DiscriminatorType.STRING,
        length = 15
)
@TypeDefs({@TypeDef(name = "crdb-uuid", typeClass = CockroachUUIDType.class)})
@DynamicInsert
@DynamicUpdate
public abstract class AbstractJournal<T> extends AbstractEntity<UUID> {
    @Id
    @Column(insertable = false, updatable = false, nullable = false)
    // UUID computed column with pg-uuid to CRDB UUID mapping
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "crdb-uuid", parameters = @org.hibernate.annotations.Parameter(name = "column", value = "id"))
    @JsonIgnore
    private UUID id;

    @Column(name = "tag")
    private String tag;

    @Column(name = "status")
    private String status;

    @Column(name = "sequence_no", updatable = false, nullable = false, insertable = false)
    private Long sequenceNumber;

    @Basic
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Type(type = "jsonb")
    @Column(name = "payload")
    @Basic(fetch = FetchType.LAZY)
    private T event;

    @PrePersist
    protected void onCreate() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getEvent() {
        return event;
    }

    public void setEvent(T payload) {
        this.event = payload;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String origin) {
        this.tag = origin;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }
}

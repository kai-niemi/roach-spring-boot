package io.roach.spring.identity.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "account_uuid")
@DynamicInsert
@DynamicUpdate
public class AssignedUUIDAccountEntity extends AccountEntity<UUID> {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @PrePersist
    public void init() {
        if (isNew()) {
            this.id = UUID.randomUUID();
        }
    }

    @Override
    public UUID getId() {
        return id;
    }
}


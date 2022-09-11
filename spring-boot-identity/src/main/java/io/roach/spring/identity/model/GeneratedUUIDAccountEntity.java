package io.roach.spring.identity.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "account_uuid")
public class GeneratedUUIDAccountEntity extends AccountEntity<UUID> {
    @Id
    @Column(name = "id", updatable = false, insertable = true)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Override
    public UUID getId() {
        return id;
    }
}

package io.roach.spring.identity.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import io.roach.spring.identity.config.hibernate.CockroachUUIDType;

@Entity
@Table(name = "account_uuid_db")
@TypeDefs({@TypeDef(name = "crdb-uuid", typeClass = CockroachUUIDType.class)})
@DynamicInsert
@DynamicUpdate
public class DatabaseUUIDAccountEntity extends AccountEntity<UUID> {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "crdb-uuid", parameters = @Parameter(name = "column", value = "id"))
    private UUID id;

    @Override
    public UUID getId() {
        return id;
    }
}

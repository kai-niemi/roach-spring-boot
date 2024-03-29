package io.roach.spring.transactions.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class AbstractEntity<ID extends Serializable> implements Persistable<ID> {
    @Transient
    private boolean isNew = true;

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}

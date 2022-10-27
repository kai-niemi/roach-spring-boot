package io.roach.spring.trees.core;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractPersistentEntity<T extends Serializable> implements PersistentEntity<T> {
    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    public abstract T getId();
}

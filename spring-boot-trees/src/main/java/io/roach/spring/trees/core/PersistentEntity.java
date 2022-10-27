package io.roach.spring.trees.core;

import java.io.Serializable;

public interface PersistentEntity<T extends Serializable> extends Serializable {
    boolean isNew();
}

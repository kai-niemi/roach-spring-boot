package io.roach.spring.trees.config;

import org.hibernate.dialect.CockroachDB201Dialect;
import org.hibernate.dialect.identity.CockroachDB1920IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;

public class CockroachDB221Dialect extends CockroachDB201Dialect {
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CockroachDB1920IdentityColumnSupport() {
            @Override
            public String getIdentityInsertString() {
                return "unordered_unique_rowid()";
            }
        };
    }
}


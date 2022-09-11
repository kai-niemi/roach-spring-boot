package io.roach.spring.identity.config.hibernate;

import org.hibernate.dialect.CockroachDB201Dialect;
import org.hibernate.dialect.identity.CockroachDB1920IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;

public class CockroachDialectNumericIdentity extends CockroachDB201Dialect {
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


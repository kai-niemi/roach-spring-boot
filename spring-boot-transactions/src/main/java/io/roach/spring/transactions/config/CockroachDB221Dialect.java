package io.roach.spring.transactions.config;

import org.hibernate.dialect.CockroachDB201Dialect;
import org.hibernate.dialect.identity.CockroachDB1920IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;

public class CockroachDB221Dialect extends CockroachDB201Dialect {
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CockroachDB221IdentityColumnSupport();
    }

    private static class CockroachDB221IdentityColumnSupport extends CockroachDB1920IdentityColumnSupport {
        @Override
        public String getIdentityInsertString() {
            return "unique_rowid()";
        }
    }
}

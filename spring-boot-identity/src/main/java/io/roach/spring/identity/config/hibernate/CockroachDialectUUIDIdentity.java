package io.roach.spring.identity.config.hibernate;

import org.hibernate.dialect.CockroachDB201Dialect;
import org.hibernate.dialect.identity.CockroachDB1920IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;

public class CockroachDialectUUIDIdentity extends CockroachDB201Dialect {
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CockroachDB1920IdentityColumnSupport() {
            @Override
            public String getIdentityInsertString() {
                return "gen_random_uuid()";
            }
        };
    }
}

package io.roach.spring.catalog.util;

import java.sql.Types;

import org.hibernate.dialect.CockroachDB201Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.type.StandardBasicTypes;

public class CockroachDBDialect extends CockroachDB201Dialect {
    public CockroachDBDialect() {
        registerFunction("cluster_logical_timestamp",
                new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP, "cluster_logical_timestamp()"));
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CockroachDBIdentityColumnSupport();
    }

    public static class CockroachDBIdentityColumnSupport extends IdentityColumnSupportImpl {
        @Override
        public boolean supportsIdentityColumns() {
            return true;
        }

        @Override
        public String getIdentitySelectString(String table, String column, int type) {
            switch (type) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.NUMERIC:
                    return "select unique_rowid()";
                default:
                    return "select gen_random_uuid()";
            }
        }

        @Override
        public String getIdentityColumnString(int type) {
            switch (type) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.NUMERIC:
                    return "not null default unique_rowid()";
                default:
                    return "not null default gen_random_uuid()";
            }
        }

        @Override
        public boolean hasDataTypeInIdentityColumn() {
            return true;
        }

        @Override
        public String getIdentityInsertString() {
            return "unordered_unique_rowid()";
        }
    }
}

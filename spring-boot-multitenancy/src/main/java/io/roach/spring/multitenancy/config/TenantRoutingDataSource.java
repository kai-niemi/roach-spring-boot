package io.roach.spring.multitenancy.config;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected DataSource determineTargetDataSource() {
        return super.determineTargetDataSource();
    }

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        Tenant id = TenantContext.getTenantId();
        if (id == null) {
            id = Tenant.alpha;
        }
        return id.name();
    }
}

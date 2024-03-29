package io.roach.spring.multitenancy.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {
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

package io.roach.spring.multitenancy.config;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Component
@ConfigurationProperties(prefix = "roach")
public class TenantDataSourceProperties {
    private final Logger traceLogger = LoggerFactory.getLogger("io.roach.SQL_TRACE");

    private final Map<Object, Object> dataSources = new LinkedHashMap<>();

    private final Set<String> readOnlyNames = new HashSet<>();

    public Map<Object, Object> getDataSources() {
        return dataSources;
    }

    public Object getDefaultDataSource() {
        return dataSources.get(Tenant.alpha);
    }

    public void setDataSources(Map<String, DataSourceProperties> properties) {
        properties.forEach((key, value) ->
                this.dataSources.put(key, createDataSource(key, value))
        );
    }

    public boolean isReadOnly(String poolName) {
         return readOnlyNames.contains(poolName);
    }

    private DataSource createDataSource(String poolName, DataSourceProperties properties) {
        int poolSize = Runtime.getRuntime().availableProcessors() * 4;

        HikariDataSource ds = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName(poolName);
        ds.setMaximumPoolSize(poolSize);// Should be: cluster_total_vcpu * 4 / total_pool_number
        ds.setMinimumIdle(poolSize / 2); // Should be maxPoolSize for fixed-sized pool
        ds.setAutoCommit(false);

        if (poolName.endsWith("readonly")) { // naming convention
            ds.setReadOnly(true);
            readOnlyNames.add(poolName);
        }

        ds.addDataSourceProperty("reWriteBatchedInserts", "true");
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds.addDataSourceProperty("useServerPrepStmts", "true");
        ds.addDataSourceProperty("application_name", "Multi-Tenancy Demo");

        return traceLogger.isTraceEnabled()
                ? ProxyDataSourceBuilder
                .create(ds)
                .asJson()
                .logQueryBySlf4j(SLF4JLogLevel.TRACE, traceLogger.getName())
                .build()
                : ds;
    }
}

package io.roach.spring.batch.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class DataSourceConfiguration {
    private final Logger traceLogger = LoggerFactory.getLogger("io.roach.SQL_TRACE");

    @Value("${roach.multi-value-inserts}")
    private boolean multiValueInserts;

    @Bean
    @Primary
    public DataSource primaryDataSource(@Autowired DataSourceProperties properties) {
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;

        HikariDataSource ds = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName("batch-statements");
        ds.setMaximumPoolSize(poolSize);// Should be: cluster_total_vcpu * 4 / total_pool_number
        ds.setMinimumIdle(poolSize / 2); // Should be maxPoolSize for fixed-sized pool
        ds.setAutoCommit(false);

        ds.addDataSourceProperty("reWriteBatchedInserts", multiValueInserts);
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds.addDataSourceProperty("useServerPrepStmts", "true");
        ds.addDataSourceProperty("application_name", "Spring Batch Statements");

        return traceLogger.isTraceEnabled()
                ? ProxyDataSourceBuilder
                .create(ds)
                .asJson()
                .logQueryBySlf4j(SLF4JLogLevel.TRACE, traceLogger.getName())
                .build()
                : ds;
    }
}
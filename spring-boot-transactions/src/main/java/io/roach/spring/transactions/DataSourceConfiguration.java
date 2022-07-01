package io.roach.spring.transactions;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class DataSourceConfiguration {
    private final Logger sqlTraceLogger = LoggerFactory.getLogger("SQL_TRACE");

    @Autowired
    private DataSourceProperties properties;

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        HikariDataSource ds = hikariDataSource();
        return sqlTraceLogger.isTraceEnabled()
                ? ProxyDataSourceBuilder
                .create(ds)
                .asJson()
                .logQueryBySlf4j(SLF4JLogLevel.TRACE, sqlTraceLogger.getName())
//                .multiline()
                .build()
                : ds;
    }

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName("spring-boot-transactions");
        ds.setMaximumPoolSize(50);// Should be: cluster_total_vcpu * 4 / total_pool_number
        ds.setMinimumIdle(25); // Should be maxPoolSize for fixed-sized pool
        ds.setAutoCommit(true); // In combo with Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT=true

        ds.addDataSourceProperty("reWriteBatchedInserts", "true");
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds.addDataSourceProperty("useServerPrepStmts", "true");
        ds.addDataSourceProperty("application_name", "Spring Boot Transactions");

        return ds;
    }

    @PostConstruct
    public void flywayMigrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(hikariDataSource())
                .load();
        flyway.repair();
        flyway.migrate();
    }
}

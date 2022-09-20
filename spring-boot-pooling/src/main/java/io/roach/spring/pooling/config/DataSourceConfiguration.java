package io.roach.spring.pooling.config;

import javax.sql.DataSource;

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
                .multiline()
                .build()
                : ds;
    }

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName("spring-boot-pooling");
        ds.setMaximumPoolSize(50);// Should be cluster_total_vcpus * 4 / num_pool_instances
        ds.setMinimumIdle(25); // Should be same as maxPoolSize for fixed-sized pool
        ds.setAutoCommit(false); // Paired with Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT=false

        ds.addDataSourceProperty("reWriteBatchedInserts", "true");
        ds.addDataSourceProperty("application_name", "Spring Boot Pooling");

        return ds;
    }
}

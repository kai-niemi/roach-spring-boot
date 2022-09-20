package io.roach.spring.pooling.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class DataSourceConfiguration {
    private final Logger sqlTraceLogger = LoggerFactory.getLogger("SQL_TRACE");

    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

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
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName("spring-boot-pooling");
        // Paired with Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT=true
        ds.setAutoCommit(false);
        // Batch inserts (PSQL JDBC driver specific, case-sensitive)
        ds.addDataSourceProperty("reWriteBatchedInserts", "true");
        // For observability in DB console
        ds.addDataSourceProperty("application_name", "Spring Boot Pooling");

        // Configured via application.yml and CLI override
//        ds.setMaximumPoolSize(50);
//        ds.setMinimumIdle(25);

        // Applies if min idle < max pool size
//        ds.setKeepaliveTime(60000);
//        ds.setMaxLifetime(1800000);
//        ds.setConnectionTimeout(10000);
//        ds.setConnectionInitSql("select 1");

        return ds;
    }
}

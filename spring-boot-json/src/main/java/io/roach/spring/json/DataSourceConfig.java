package io.roach.spring.json;

import javax.sql.DataSource;

import org.postgresql.PGProperty;
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
public class DataSourceConfig {
    private final Logger sqlTraceLogger = LoggerFactory.getLogger("io.roach.SQL_TRACE");

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
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName("spring-boot-json");
        ds.addDataSourceProperty(PGProperty.REWRITE_BATCHED_INSERTS.getName(), "true");
        ds.addDataSourceProperty("application_name", "Spring Boot JSON");
        ds.setAutoCommit(false); // Paired with Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT=true
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(5);
        ds.setInitializationFailTimeout(-1);
        return ds;
    }
}

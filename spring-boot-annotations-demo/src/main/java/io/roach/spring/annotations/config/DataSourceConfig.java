package io.roach.spring.annotations.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class DataSourceConfig {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    @ConfigurationProperties("roach.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("roach.datasource.configuration")
    public HikariDataSource primaryDataSource() {
        return dataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = primaryDataSource();

        logger.info("Connection pool max size: {}", dataSource.getMaximumPoolSize());
        logger.info("Connection pool idle timeout: {}", dataSource.getIdleTimeout());
        logger.info("Connection pool max lifetime: {}", dataSource.getMaxLifetime());
        logger.info("Connection pool validation timeout: {}", dataSource.getValidationTimeout());

        if (logger.isDebugEnabled()) {
            logger.info("Wrapping data source in trace logging proxy");
            return new LazyConnectionDataSourceProxy(ProxyDataSourceBuilder
                    .create(dataSource)
                    .name("SQL-Trace")
                    .asJson()
                    .countQuery()
                    .logQueryBySlf4j(SLF4JLogLevel.DEBUG, "io.roach.SQL_TRACE")
//                    .multiline()
                    .build());
        }

        return new LazyConnectionDataSourceProxy(dataSource);
    }
}

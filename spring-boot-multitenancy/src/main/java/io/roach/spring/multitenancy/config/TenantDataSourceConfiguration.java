package io.roach.spring.multitenancy.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TenantDataSourceConfiguration {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TenantDataSourceProperties dataSourceProperties;

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(dataSourceProperties.getDefaultDataSource());
        routingDataSource.setTargetDataSources(dataSourceProperties.getDataSources());
        routingDataSource.setLenientFallback(false);
        return routingDataSource;
    }

    @PostConstruct
    public void flywayMigrate() {
        dataSourceProperties
                .getDataSources()
                .keySet()
                .stream()
                .map(id -> (String) id)
                .forEach(id -> {
                    if (!dataSourceProperties.isReadOnly(id)) {
                        DataSource dataSource = (DataSource) dataSourceProperties
                                .getDataSources()
                                .get(id);
                        Flyway flyway = Flyway.configure()
                                .dataSource(dataSource)
                                .load();
                        flyway.repair();
                        flyway.migrate();
                    } else {
                        logger.info("Skipping {} - read only datasource", id);
                    }
                });
    }
}

package io.roach.spring.multitenancy.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfiguration extends AbstractDataSourceConfiguration {
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
                .values()
                .stream()
                .map(dataSource -> (DataSource) dataSource)
                .forEach(dataSource -> {
                    Flyway flyway = Flyway.configure()
                            .dataSource(dataSource)
                            .load();
                    flyway.repair();
                    flyway.migrate();
                });
    }
}

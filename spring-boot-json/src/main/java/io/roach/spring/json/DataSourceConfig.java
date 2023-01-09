package io.roach.spring.json;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        ChainListener listener = new ChainListener();
        listener.addListener(new DataSourceQueryCountListener());
        return ProxyDataSourceBuilder
                .create(properties.initializeDataSourceBuilder().build())
                .name("SQL-Trace")
                .listener(listener)
                .asJson()
                .countQuery()
                .logQueryBySlf4j(SLF4JLogLevel.DEBUG, "io.roach.SQL_TRACE")
                .multiline()
                .build();
    }
}

package io.roach.spring.order.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cfg.Environment;
import org.postgresql.PGProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import io.roach.spring.order.util.CockroachDBDialect;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * Configuration for the repository/database layer including transaction management.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"io.roach"})
public class JpaConfiguration {
    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = hikariDataSource();
        return
                ProxyDataSourceBuilder
                        .create(new LazyConnectionDataSourceProxy(dataSource))
                        .name("SQL-Trace")
                        .asJson()
                        .logQueryBySlf4j(SLF4JLogLevel.TRACE, "io.roach.SQL_TRACE")
                        .multiline()
                        .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setAutoCommit(false);
//        ds.addDataSourceProperty(PGProperty.REWRITE_BATCHED_INSERTS.getName(), "true");
        ds.addDataSourceProperty("application_name", "Roach Order Service");
        return ds;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Autowired EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setJpaDialect(new HibernateJpaDialect());
        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("io.roach");
        emf.setJpaProperties(jpaVendorProperties());
        emf.setJpaVendorAdapter(jpaVendorAdapter());
        return emf;
    }

    private Properties jpaVendorProperties() {
        return new Properties() {
            {
//                setProperty(Environment.STATEMENT_BATCH_SIZE, "128");
//                setProperty(Environment.BATCH_VERSIONED_DATA, "true");
                setProperty(Environment.ORDER_INSERTS, "true");
                setProperty(Environment.ORDER_UPDATES, "true");

                setProperty(Environment.GENERATE_STATISTICS, Boolean.TRUE.toString());
                setProperty(Environment.LOG_SESSION_METRICS, Boolean.FALSE.toString());
                setProperty(Environment.USE_MINIMAL_PUTS, "true");
                setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
                setProperty(Environment.CACHE_REGION_FACTORY, NoCachingRegionFactory.class.getName());
                setProperty(Environment.FORMAT_SQL, "false");
                setProperty(Environment.NON_CONTEXTUAL_LOB_CREATION, "true");
                setProperty(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
            }
        };
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        vendorAdapter.setDatabasePlatform(CockroachDBDialect.class.getName());
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        return vendorAdapter;
    }
}

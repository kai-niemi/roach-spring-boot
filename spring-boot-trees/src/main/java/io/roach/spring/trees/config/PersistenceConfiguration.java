package io.roach.spring.trees.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class PersistenceConfiguration {
    private final Logger sqlTraceLogger = LoggerFactory.getLogger("io.roach.SQL_TRACE");

    @Autowired
    private DataSourceProperties properties;

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        HikariDataSource ds = hikariDataSource();
        return ProxyDataSourceBuilder
                .create(ds)
                .asJson()
                .logQueryBySlf4j(SLF4JLogLevel.TRACE, sqlTraceLogger.getName())
                .multiline()
                .build();
    }

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setAutoCommit(false); // Paired with Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT=false
        ds.addDataSourceProperty("reWriteBatchedInserts", "true");
        ds.addDataSourceProperty("application_name", "Spring Boot Identity");
        return ds;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Autowired EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setJpaDialect(new HibernateJpaDialect());
        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Autowired DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName(getClass().getSimpleName());
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("io.roach.spring");
        emf.setJpaVendorAdapter(jpaVendorAdapter());
        emf.setJpaProperties(jpaVendorProperties());
        return emf;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        vendorAdapter.setDatabasePlatform(CockroachDB221Dialect.class.getName());
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        return vendorAdapter;
    }

    private Properties jpaVendorProperties() {
        return new Properties() {
            {
                setProperty(Environment.STATEMENT_BATCH_SIZE, "32");
                setProperty(Environment.ORDER_INSERTS, "true");
                setProperty(Environment.ORDER_UPDATES, "true");
                setProperty(Environment.BATCH_VERSIONED_DATA, "true");
                setProperty(Environment.GENERATE_STATISTICS, "true");
                setProperty(Environment.LOG_SESSION_METRICS, "false");
                setProperty(Environment.CACHE_REGION_FACTORY, NoCachingRegionFactory.class.getName());
                setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
                setProperty(Environment.USE_MINIMAL_PUTS, "true");
                setProperty(Environment.FORMAT_SQL, "false");
                setProperty(Environment.NON_CONTEXTUAL_LOB_CREATION, "true"); // in dialect also
                setProperty(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
            }
        };
    }
}

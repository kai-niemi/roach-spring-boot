package io.roach.spring.multitenancy.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.CockroachDB201Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public abstract class AbstractDataSourceConfiguration {
    //    @Value("${roach.batch-size}")
    private int batchSize = 12;

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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Autowired DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName(getClass().getSimpleName());
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("io.roach.spring.multitenancy");
        emf.setJpaVendorAdapter(jpaVendorAdapter());
        emf.setJpaProperties(jpaVendorProperties());
        return emf;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        vendorAdapter.setDatabasePlatform(CockroachDB201Dialect.class.getName());
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        return vendorAdapter;
    }

    private Properties jpaVendorProperties() {
        return new Properties() {
            {
                setProperty(Environment.STATEMENT_BATCH_SIZE, "" + batchSize);
                setProperty(Environment.ORDER_INSERTS, Boolean.TRUE.toString());
                setProperty(Environment.ORDER_UPDATES, Boolean.TRUE.toString());
                setProperty(Environment.BATCH_VERSIONED_DATA, Boolean.TRUE.toString());

                setProperty(Environment.GENERATE_STATISTICS, Boolean.TRUE.toString());
                setProperty(Environment.LOG_SESSION_METRICS, Boolean.FALSE.toString());
                setProperty(Environment.USE_MINIMAL_PUTS, Boolean.TRUE.toString());
                setProperty(Environment.USE_SECOND_LEVEL_CACHE, Boolean.FALSE.toString());
                setProperty(Environment.CACHE_REGION_FACTORY, NoCachingRegionFactory.class.getName());
                setProperty(Environment.FORMAT_SQL, Boolean.FALSE.toString());
                // Mutes Postgres JPA Error (Method org.postgresql.jdbc.PgConnection.createClob() is not yet implemented).
                setProperty(Environment.NON_CONTEXTUAL_LOB_CREATION, Boolean.TRUE.toString());
                setProperty(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, Boolean.TRUE.toString());
            }
        };
    }
}

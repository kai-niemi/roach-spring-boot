package io.roach.spring.identity.config;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

@Configuration
public class PersistenceConfiguration {
    @Value("${spring.datasource.batch-size}")
    private int batchSize;

    @Value("${spring.datasource.dialect}")
    private String dialect;

    @PostConstruct
    public void init() {
        Assert.isTrue(batchSize > 0, "batchSize is <= 0");
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
        vendorAdapter.setDatabasePlatform(dialect);
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        return vendorAdapter;
    }

    private Properties jpaVendorProperties() {
        return new Properties() {
            {
                setProperty(Environment.STATEMENT_BATCH_SIZE, "" + batchSize);
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
//                setProperty(Environment.CHECK_NULLABILITY, "true");
            }
        };
    }
}

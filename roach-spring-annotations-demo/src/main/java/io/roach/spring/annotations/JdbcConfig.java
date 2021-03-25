package io.roach.spring.annotations;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import io.roach.spring.annotations.aspect.RetryableSavepointAspect;

@Configuration
@EnableJdbcRepositories
@Profile("jdbc")
public class JdbcConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    protected void init() {
        logger.info("Using JDBC and savepoints for retry's");
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        transactionManager.setEnforceReadOnly(true);
        transactionManager.setNestedTransactionAllowed(true);
        transactionManager.setRollbackOnCommitFailure(false);
        transactionManager.setDefaultTimeout(-1);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    // Savepoints only works with JDBC
    @Bean
    public RetryableSavepointAspect retryableSavepointAspect() {
        return new RetryableSavepointAspect("cockroach_restart");
    }
}

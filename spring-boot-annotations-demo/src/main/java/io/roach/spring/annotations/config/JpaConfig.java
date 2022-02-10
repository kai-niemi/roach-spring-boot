package io.roach.spring.annotations.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.roach.spring.annotations.aspect.AdvisorOrder;
import io.roach.spring.annotations.aspect.RetryableAspect;

@Configuration
@EnableTransactionManagement(order = AdvisorOrder.HIGH, proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"io.roach.spring.annotations"})
@Profile("jpa")
public class JpaConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    protected void init() {
        logger.info("Using JPA (no savepoints)");
    }

    @Bean
    public RetryableAspect retryableTransactionalAspect() {
        return new RetryableAspect();
    }
}

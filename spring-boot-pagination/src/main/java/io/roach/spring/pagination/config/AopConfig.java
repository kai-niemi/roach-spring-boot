package io.roach.spring.pagination.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.RetryConfiguration;

import io.roach.spring.annotations.aspect.RetryableAspect;
import io.roach.spring.annotations.aspect.TransactionHintsAspect;

@Configuration
public class AopConfig extends RetryConfiguration {
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE-1;
    }

    @Bean
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }

    @Bean
    public RetryableAspect retryableTransactionalAspect() {
        return new RetryableAspect();
    }
}

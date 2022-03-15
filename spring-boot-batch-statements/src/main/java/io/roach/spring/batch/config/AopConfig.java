package io.roach.spring.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.roach.spring.annotations.aspect.RetryableAspect;
import io.roach.spring.annotations.aspect.TransactionHintsAspect;

@Configuration
public class AopConfig {
    @Bean
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }

    @Bean
    public RetryableAspect retryableTransactionalAspect() {
        return new RetryableAspect();
    }
}
